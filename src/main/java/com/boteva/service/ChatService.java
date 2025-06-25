package com.boteva.service;

import com.boteva.model.ChatMessage;
import com.boteva.model.Transaction;
import com.boteva.repository.ClientRepository;
import com.boteva.repository.TransactionRepository;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final TransactionRepository transactionRepository;
    private final ClientRepository clientRepository;

    private final Map<String, List<ChatMessage>> sessionHistory = new ConcurrentHashMap<>();

    private final OpenAiService openAiService;

    @Value("${openai.model:gpt-4}")
    private String openAiModel;

    /**
     * Handles a user message, appends it to history, builds context, and gets AI response.
     */
    public ChatMessage chatWithAssistant(Long clientId, List<ChatMessage> messages) {
        List<ChatMessage> history = new ArrayList<>(messages);

        // Inject system message if missing
        if (history.stream().noneMatch(msg -> "system".equals(msg.getRole()))) {
            history.add(0, buildSystemMessage(clientId));
        }

        // Convert chat to OpenAI format
        List<com.theokanning.openai.completion.chat.ChatMessage> openAiMessages = history.stream()
            .map(msg -> new com.theokanning.openai.completion.chat.ChatMessage(msg.getRole(), msg.getMessage()))
            .collect(Collectors.toList());

        // Create OpenAI chat request
        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .model(openAiModel)
            .messages(openAiMessages)
            .temperature(0.7)
            .maxTokens(500)
            .build();

        // Call OpenAI
        String aiReply = openAiService.createChatCompletion(request)
            .getChoices().get(0).getMessage().getContent();

        // Build assistant message
        ChatMessage response = ChatMessage.builder()
            .role("assistant")
            .message(aiReply)
            .timestamp(LocalDateTime.now())
            .build();

        return response;
    }

    /**
     * Build a system message based on the last 3 months of transaction history.
     */
    private ChatMessage buildSystemMessage(Long clientId) {
        var client = clientRepository.findById(clientId)
        .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));

        LocalDate fromDate = LocalDate.now().minusMonths(3);
        List<Transaction> txs = transactionRepository.findByClientIdAndDateAfter(clientId, fromDate);

        if (txs.isEmpty()) {
            System.out.println("no financial data available for client: " + clientId);
            return ChatMessage.builder()
                .role("system")
                .message("""
                    Si osobný bankový asistent s umelou inteligenciou.
                    Momentálne nemáme dostupné žiadne finančné údaje o klientovi.
                    Poskytuj všeobecné rady, ako šetriť peniaze a pritom si udržať súčasný životný štýl.
                    Buď priateľský, praktický a motivujúci.
                """)
                .timestamp(LocalDateTime.now())
                .build();
        }

        // Summarize transactions by category
        String summary = summarizeTransactions(txs);

        String sysPrompt = """
            Si osobný bankový asistent s umelou inteligenciou.
            Tvojou úlohou je pomôcť klientovi %s udržať si svoj životný štýl, a zároveň zlepšiť jeho finančnú disciplínu.

            Na základe transakcií z posledných 3 mesiacov sú výdavkové návyky klienta nasledovné:
            %s

            Poskytuj konkrétne a realistické odporúčania.
            Buď povzbudivý a empatický, nikdy neodsudzujúci.
            Vyhni sa radikálnym škrtom, pokiaľ nie sú úplne nevyhnutné.
            Tvoje rady by mali byť praktické, ľahko uskutočniteľné a mali by pomôcť klientovi ušetriť peniaze bez výrazného obmedzenia kvality života.
        """.formatted(client.getName(), summary);

        System.out.println("system prompt for client %s:\n%s".formatted(clientId, sysPrompt));

        return ChatMessage.builder()
            .role("system")
            .message(sysPrompt)
            .timestamp(LocalDateTime.now())
            .build();
    }

    /**
     * Group and summarize transactions by category.
     */
    private String summarizeTransactions(List<Transaction> txs) {
        Map<String, BigDecimal> totals = txs.stream()
            .collect(Collectors.groupingBy(
                Transaction::getCategory,
                Collectors.mapping(
                    Transaction::getAmount,
                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                )
            ));

        return totals.entrySet().stream()
            .map(e -> "- %s: $%.2f".formatted(e.getKey(), e.getValue()))
            .collect(Collectors.joining("\n"));
    }

    /**
     * Get conversation history.
     */
    public List<ChatMessage> getChatHistory(String clientId) {
        return sessionHistory.getOrDefault(clientId, List.of());
    }

    /**
     * Reset the chat session for a client.
     */
    public void resetChat(String clientId) {
        sessionHistory.remove(clientId);
    }
}
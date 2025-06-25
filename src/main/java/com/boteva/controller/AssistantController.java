package com.boteva.controller;

import com.boteva.model.ChatMessage;
import com.boteva.service.ChatService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/assistant")
@RequiredArgsConstructor
public class AssistantController {

    private final ChatService chatService;

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody ChatRequest request) {
        ChatMessage chatMessage = chatService.chatWithAssistant(request.getClientId(), request.getMessage());
        return ResponseEntity.ok(Map.of("message", chatMessage.getMessage()));
    }

    @Data
    public static class ChatRequest {
        private Long clientId;
        private String message;
    }
}

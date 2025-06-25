package com.boteva.service;

import com.boteva.model.Transaction;
import com.boteva.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<Transaction> getRecentTransactions(Long clientId, int monthsBack) {
        LocalDate from = LocalDate.now().minusMonths(monthsBack);
        return transactionRepository.findByClientIdAndDateAfter(clientId, from);
    }

    public BigDecimal getTotalSpending(List<Transaction> txs) {
        return txs.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getSpendingByCategory(List<Transaction> txs, String category) {
        return txs.stream()
                .filter(tx -> tx.getCategory().equalsIgnoreCase(category))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
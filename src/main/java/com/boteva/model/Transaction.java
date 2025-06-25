package com.boteva.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to the Client entity instead of clientId String
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // Category of expense (e.g. "Groceries", "Entertainment", "Rent")
    @Column(nullable = false)
    private String category;

    // Description of the transaction (e.g. "Netflix", "Trader Joe's")
    private String description;

    // Amount in USD (can be negative if needed)
    @Column(nullable = false)
    private BigDecimal amount;

    // Date when the transaction occurred
    @Column(nullable = false)
    private LocalDate date;
}

package com.boteva.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private LocalDate joinedDate;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
    
    // Helper method to add a transaction
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        transaction.setClient(this);
    }

    // Helper method to remove a transaction
    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
        transaction.setClient(null);
    }
}

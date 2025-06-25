package com.boteva.repository;

import com.boteva.model.Transaction;
import com.boteva.model.Client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Fetch all transactions for a client from a given date forward.
     */
    @Query("SELECT t FROM Transaction t WHERE t.client.id = :clientId AND t.date >= :fromDate ORDER BY t.date DESC")
    List<Transaction> findByClientIdAndDateAfter(@Param("clientId") Long clientId, @Param("fromDate") LocalDate fromDate);

    /**
     * Optionally fetch all transactions for a client.
     */
    List<Transaction> findByClientIdOrderByDateDesc(Client client);
}
package com.boteva.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

import com.boteva.repository.ClientRepository;
import com.boteva.repository.TransactionRepository;
import com.boteva.model.Client;
import com.boteva.model.Transaction;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ClientRepository clientRepository;
    private final TransactionRepository transactionRepository;
    private final Random random = new Random();

    public DataSeeder(ClientRepository clientRepository, TransactionRepository transactionRepository) {
        this.clientRepository = clientRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (clientRepository.count() == 0) {
            seedClientsAndTransactions();
        }
    }

    private void seedClientsAndTransactions() {
        // Create clients
        Client alice = Client.builder()
                .name("Alice")
                .email("alice@example.com")
                .joinedDate(LocalDate.now().minusMonths(6))
                .build();

        Client bob = Client.builder()
                .name("Bob")
                .email("bob@example.com")
                .joinedDate(LocalDate.now().minusMonths(8))
                .build();

        Client carol = Client.builder()
                .name("Carol")
                .email("carol@example.com")
                .joinedDate(LocalDate.now().minusMonths(10))
                .build();

        clientRepository.save(alice);
        clientRepository.save(bob);
        clientRepository.save(carol);

        // Seed transactions for past 3 months
        LocalDate startDate = LocalDate.now().minusMonths(3);
        LocalDate endDate = LocalDate.now();

        seedTransactionsForClient(alice, startDate, endDate, SpendingProfile.AGGRESSIVE);
        seedTransactionsForClient(bob, startDate, endDate, SpendingProfile.MODERATE);
        seedTransactionsForClient(carol, startDate, endDate, SpendingProfile.CONSERVATIVE);
    }

    private void seedTransactionsForClient(Client client, LocalDate start, LocalDate end, SpendingProfile profile) {
        LocalDate date = start;
        while (!date.isAfter(end)) {
            int transactionsCount = profile.getDailyTransactionCount(random);

            for (int i = 0; i < transactionsCount; i++) {
                Transaction tx = Transaction.builder()
                        .client(client)
                        .category(profile.getRandomCategory(random))
                        .description(profile.getRandomDescription(random))
                        .amount(profile.getRandomAmount(random))
                        .date(date)
                        .build();

                transactionRepository.save(tx);
            }

            date = date.plusDays(1);
        }
    }

    private enum SpendingProfile {
        AGGRESSIVE(
                new String[]{"Clothes", "Food", "Entertainment", "Travel", "Electronics", "Gifts", "Health", "Private Transport"},
                new String[]{"Bought shoes", "Dinner out", "Concert tickets", "Flight to NYC", "New headphones", "Birthday gift", "Gym membership", "Fuel"},
                3, 6,
                20, 200
        ),
        MODERATE(
                new String[]{"Clothes", "Food", "Entertainment", "Travel", "Electronics", "Gifts", "Health"},
                new String[]{"Bought jacket", "Groceries", "Movie night", "Weekend trip", "Phone case", "Gift card", "Pharmacy"},
                1, 3,
                10, 100
        ),
        CONSERVATIVE(
                new String[]{"Food", "Utilities", "Groceries", "Health", "Subscriptions", "Savings", "Public Transport"},
                new String[]{"Cooked at home", "Electricity bill", "Weekly groceries", "Doctor visit", "Streaming service", "Monthly saving", "Bus fare"},
                0, 2,
                5, 50
        );

        private final String[] categories;
        private final String[] descriptions;
        private final int minTx;
        private final int maxTx;
        private final int minAmount;
        private final int maxAmount;

        SpendingProfile(String[] categories, String[] descriptions, int minTx, int maxTx, int minAmount, int maxAmount) {
            this.categories = categories;
            this.descriptions = descriptions;
            this.minTx = minTx;
            this.maxTx = maxTx;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
        }

        public int getDailyTransactionCount(Random rand) {
            return rand.nextInt(maxTx - minTx + 1) + minTx;
        }

        public String getRandomCategory(Random rand) {
            return categories[rand.nextInt(categories.length)];
        }

        public String getRandomDescription(Random rand) {
            return descriptions[rand.nextInt(descriptions.length)];
        }

        public BigDecimal getRandomAmount(Random rand) {
            double amount = minAmount + (maxAmount - minAmount) * rand.nextDouble();
            return BigDecimal.valueOf(Math.round(amount * 100) / 100.0);
        }
    }
}

package intermediate;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class MostRecentTransactionByMonth {

    // Problem 3: Group by Month and Most Recent Transaction
    @SuppressWarnings("all")
    public static class Transaction {
        LocalDate date;
        String id;
        double amount;

        public Transaction(LocalDate date, String id, double amount) {
            this.date = date;
            this.id = id;
            this.amount = amount;
        }

        public LocalDate getDate() { return date; }
        public String getId() { return id; }
        public double getAmount() { return amount; }

        @Override
        public String toString() {
            return String.format("Transaction[date=%s, id=%s, amount=%.2f]", date, id, amount);
        }
    }

    /**
     * Finds the most recent transaction for each month.
     * 
     * @param transactions List of transactions to analyze
     * @return Map where:
     *         - Key: YearMonth
     *         - Value: Most recent transaction in that month
     */
    public static Map<YearMonth, Transaction> mostRecentTransactionByMonth(List<Transaction> transactions) {
        return transactions.stream().collect(
                // Group transactions by month
                Collectors.groupingBy(
                        transaction -> YearMonth.from(transaction.getDate()),
                        // For each month, find the most recent transaction
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(Transaction::getDate)),
                                Optional::get  // Unwrap the Optional to get the Transaction
                        )
                )
        );
    }

    /**
     * Finds the transaction with the highest amount for each month.
     * If multiple transactions have the same highest amount, the most recent one is chosen.
     *
     * @param transactions List of transactions to analyze
     * @return Map where:
     *         - Key: YearMonth
     *         - Value: Transaction with the highest amount in that month (most recent if tied)
     */
    public static Map<YearMonth, Transaction> highestAmountTransactionByMonth(List<Transaction> transactions) {
        return transactions.stream().collect(
                // Group transactions by month
                Collectors.groupingBy(
                        d -> YearMonth.from(d.getDate()),
                        // For each month, find the transaction with the highest amount
                        // If amounts are equal, choose the most recent one
                        Collectors.collectingAndThen(
                                Collectors.maxBy(
                                        Comparator.comparing(Transaction::getAmount)
                                                .thenComparing(Transaction::getDate)
                                ),
                                Optional::get  // Unwrap the Optional to get the Transaction
                        )
                )
        );
    }

    public static void main(String[] args) {
        // Problem 3: Transaction Analysis
        // Test data with transactions across different months
        List<Transaction> transactions = List.of(
                new Transaction(LocalDate.of(2023, 1, 10), "T1", 200),  // January
                new Transaction(LocalDate.of(2023, 1, 15), "T2", 300),  // January
                new Transaction(LocalDate.of(2023, 3, 15), "T2", 300),  // March
                new Transaction(LocalDate.of(2023, 3, 21), "T2", 200),  // March
                new Transaction(LocalDate.of(2023, 2, 15), "T2", 50),   // February
                new Transaction(LocalDate.of(2023, 2, 5), "T3", 150)    // February
        );

        // A: Find most recent transaction for each month
        System.out.println("Problem 3 A: " + mostRecentTransactionByMonth(transactions));

        // B: Find transaction with the highest amount for each month
        System.out.println("Problem 3 B: " + highestAmountTransactionByMonth(transactions));
    }
}

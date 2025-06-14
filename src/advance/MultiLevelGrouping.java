package advance;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MultiLevelGrouping {
    /**
     * Represents a sale transaction with product details and revenue.
     */
    record Sale(String productId, String category, double revenue, LocalDate date) {}
    
    /**
     * Represents aggregated sales data for a category in a specific year.
     * @param totalRevenue Total revenue for the category in the year
     * @param uniqueProductCount Number of unique products sold
     */
    record CategoryYearSales(double totalRevenue, long uniqueProductCount) {}

    /**
     * Summarizes sales data by category and year, calculating total revenue
     * and counting unique products for each category-year combination.
     * 
     * @param sales List of sales transactions to analyze
     * @return A nested map where:
     *         - Outer key: Category name
     *         - Inner key: Year
     *         - Value: CategoryYearSales containing total revenue and unique product count
     */
    public static Map<String, Map<Integer, CategoryYearSales>> summarizeSalesByCategoryAndYear(List<Sale> sales) {
        // TODO: Implement the multi-level grouping and custom aggregation.
        // HINT: Use Collectors.groupingBy for the two levels. For the downstream collector,
        // you might need Collectors.teeing or another complex collector to build CategoryYearSales.
        return sales.stream().collect(Collectors.groupingBy(
                Sale::category,
                Collectors.groupingBy(
                        sale -> sale.date.getYear(),
                        Collectors.teeing(
                            Collectors.summarizingDouble(Sale::revenue),
                                Collectors.toMap(Sale::productId, Function.identity()),
                                (s, d) -> {
                                    return new CategoryYearSales(s.getSum(), d.size());
                                }
                        )
                )
        ));
    }

    public static void main(String[] args) {
        // Test data with sales across different categories and years
        List<Sale> sales = List.of(
                // Electronics category
                new Sale("P1", "Electronics", 250.0, LocalDate.of(2023, 1, 15)),    // Electronics 2023: P1
                new Sale("P2", "Electronics", 350.0, LocalDate.of(2023, 3, 20)),    // Electronics 2023: P2
                new Sale("P1", "Electronics", 280.0, LocalDate.of(2024, 2, 10)),    // Electronics 2024: P1
                
                // Books category
                new Sale("P3", "Books", 30.0, LocalDate.of(2023, 5, 5)),           // Books 2023: P3
                new Sale("P4", "Books", 45.0, LocalDate.of(2023, 5, 12)),          // Books 2023: P4
                new Sale("P3", "Books", 35.0, LocalDate.of(2024, 4, 1)),           // Books 2024: P3
                
                // Clothing category
                new Sale("P5", "Clothing", 80.0, LocalDate.of(2023, 8, 30))        // Clothing 2023: P5
        );

        // Generate summary using the collector
        Map<String, Map<Integer, CategoryYearSales>> summary = summarizeSalesByCategoryAndYear(sales);

        // Print the summary in a readable format
        System.out.println("Sales Summary by Category and Year:");
        summary.forEach((category, yearMap) -> {
            System.out.println("- " + category);
            yearMap.forEach((year, salesData) -> System.out.printf("  %d: %s\n", year, salesData));
        });

        // Verify the results
        // Electronics 2023: 2 unique products (P1, P2), total revenue 600.0 (250.0 + 350.0)
        assert summary.get("Electronics").get(2023).totalRevenue() == 600.0;
        assert summary.get("Electronics").get(2023).uniqueProductCount() == 2;
        
        // Electronics 2024: 1 unique product (P1), total revenue 280.0
        assert summary.get("Electronics").get(2024).totalRevenue() == 280.0;
        assert summary.get("Electronics").get(2024).uniqueProductCount() == 1;
        
        // Books 2023: 2 unique products (P3, P4), total revenue 75.0 (30.0 + 45.0)
        assert summary.get("Books").get(2023).totalRevenue() == 75.0;
        assert summary.get("Books").get(2023).uniqueProductCount() == 2;
        
        System.out.println("\nTest Passed! ✅");
    }
}

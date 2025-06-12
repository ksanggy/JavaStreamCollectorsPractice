package intermediate;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class TopNProductsPerCategory {
    // Problem 4: Top N Products Per Category
    /**
     * Represents a product with a category and rating.
     * Note: Products with equal ratings will be ordered non-deterministically
     * as we only sort by rating.
     */
    public static class Product {
        String category;
        double rating;

        public Product(String category, double rating) {
            this.category = category;
            this.rating = rating;
        }

        public String getCategory() { return category; }
        public double getRating() { return rating; }

        @Override
        public String toString() {
            return "Product[category=%s, rating=%s]".formatted(category, rating);
        }
    }

    /**
     * Finds the top N products with highest ratings for each category.
     * Note: When multiple products have the same rating, their order is non-deterministic
     * as we only sort by rating. For consistent ordering with equal ratings,
     * consider adding a secondary sort criteria.
     * 
     * @param products List of products to analyze
     * @param n Number of top products to return per category
     * @return Map where:
     *         - Key: Category name
     *         - Value: List of top N products in that category, sorted by rating (highest first)
     */
    public static Map<String, List<Product>> topNProductsPerCategory(List<Product> products, int n) {
        return products.stream().collect(
                // First, group all products by their category
                Collectors.collectingAndThen(
                        Collectors.groupingBy(
                                Product::getCategory,
                                Collectors.toList()
                        ),
                        // Then, transform each category's product list to get top N by rating
                        groupedMap -> groupedMap.entrySet().stream()
                                .collect(Collectors.toMap(
                                        // Keep the same category as the key
                                        Map.Entry::getKey,
                                        // For each category's products:
                                        entry ->
                                                entry.getValue().stream()
                                                        // Sort by rating in descending order
                                                        .sorted(Comparator.comparing(Product::getRating).reversed())
                                                        // Take only the top N products
                                                        .limit(n)
                                                        // Collect into a list
                                                        .collect(Collectors.toList())
                                ))
                )
        );
    }

    public static void main(String[] args) {
        // Problem 4: Test data with various products and ratings
        List<Product> products = List.of(
                new Product("Electronics", 4.5),  // Will be included in top 2 for Electronics
                new Product("Electronics", 4.3),  // Will be included in top 2 for Electronics
                new Product("Electronics", 3.1),  // Will be excluded (not in top 2)
                new Product("Electronics", 4.8),  // Will be included in top 2 for Electronics
                new Product("Books", 3.9),        // Will be included in top 2 for Books
                new Product("Books", 4.0),        // Will be included in top 2 for Books
                new Product("Books", 2.6),        // Will be excluded (not in top 2)
                new Product("Books", 5.0),        // Will be included in top 2 for Books
                new Product("Books", 4.2)         // Will be included in top 2 for Books
        );
        // Expected output will show top 2 products per category, sorted by rating
        // Note: For equal ratings, the order is non-deterministic
        System.out.println("Problem 4: " + topNProductsPerCategory(products, 2));
    }
}

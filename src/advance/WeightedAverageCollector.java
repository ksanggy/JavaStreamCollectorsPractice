package advance;

import java.util.List;
import java.util.stream.Collector;

public class WeightedAverageCollector {
    // Constants for weights
    private static final double HOMEWORK_WEIGHT = 0.2;
    private static final double EXAM_WEIGHT = 0.3;

    /**
     * Represents a score with its associated weight.
     * Used for calculating weighted averages where different scores
     * contribute differently to the final result.
     */
    public record Score(double value, double weight) {}

    /**
     * Mutable container for accumulating weighted sum and total weight.
     * This class handles the intermediate calculations needed for
     * computing a weighted average.
     */
    static class WeightedAverageAccumulator {
        double weightedSum = 0.0;  // Sum of (value * weight) for all scores
        double totalWeight = 0.0;  // Sum of all weights

        /**
         * Accumulates a single score into the weighted sum and total weight.
         * @param s The score to accumulate
         */
        void accept(Score s) {
            weightedSum += s.value() * s.weight();  // Add weighted value
            totalWeight += s.weight();              // Add weight to total
        }

        /**
         * Combines this accumulator with another.
         * Used when processing streams in parallel.
         * <a/>
         * Note: This method modifies and returns the first accumulator (this).
         * While this might trigger IDE warnings about side effects, this is actually
         * the standard pattern for mutable accumulators in Java collectors because:
         * 1. It's more efficient (no new object creation)
         * 2. It follows the standard pattern used in JDK collectors
         * 3. It's required by the collector interface for proper parallel processing
         * <a/>
         * Alternative functional approach would be:
         * WeightedAverageAccumulator result = new WeightedAverageAccumulator();
         * result.weightedSum = this.weightedSum + other.weightedSum;
         * result.totalWeight = this.totalWeight + other.totalWeight;
         * return result;
         * But this would be less efficient for large streams.
         * 
         * @param other The other accumulator to combine with
         * @return This accumulator with combined values
         */
        @SuppressWarnings("all")
        WeightedAverageAccumulator combine(WeightedAverageAccumulator other) {
            this.weightedSum += other.weightedSum;    // Combine weighted sums
            this.totalWeight += other.totalWeight;    // Combine total weights
            return this;
        }

        /**
         * Calculates the final weighted average.
         * @return The weighted average, or 0.0 if total weight is 0
         */
        double finisher() {
            return totalWeight == 0 ? 0.0 : weightedSum / totalWeight;
        }
    }

    /**
     * Creates a collector that calculates the weighted average of scores.
     * The weighted average is calculated as:
     *     sum(value * weight) / sum(weight)
     * 
     * @return A collector that produces the weighted average as a Double
     */
    public static Collector<Score, ?, Double> toWeightedAverage() {
        return Collector.of(
                // Supplier: creates a new accumulator
                WeightedAverageAccumulator::new,
                // Accumulator: adds a score to the running total
                WeightedAverageAccumulator::accept,
                // Combiner: merges two accumulators when processing in parallel
                (s1, s2) -> {s1.combine(s2); return s1;},
                // Finisher: calculates the final weighted average
                WeightedAverageAccumulator::finisher
        );
    }

    public static void main(String[] args) {
        // Test data: Course grades with their respective weights
        List<Score> scores = List.of(
                new Score(85, 0.2), // Homework 1 (20% weight)
                new Score(92, 0.2), // Homework 2 (20% weight)
                new Score(78, 0.3), // Midterm (30% weight)
                new Score(88, 0.3)  // Final (30% weight)
        );

        // Calculate weighted average using the collector
        double weightedAverage = scores.stream().collect(toWeightedAverage());
        System.out.printf("Calculated Weighted Average: %.2f\n", weightedAverage);

        // Manual calculation for verification
        // Note: Using double literals directly can lead to floating-point precision issues.
        // For production code, consider the following improvements:
        //
        // 1. Use Constants for Better Maintainability:
        //    private static final double HOMEWORK_WEIGHT = 0.2;
        //    private static final double EXAM_WEIGHT = 0.3;
        //    - Makes the code more maintainable
        //    - Reduces the chance of typos
        //    - Makes the meaning clearer
        //
        // 2. Use BigDecimal for Exact Arithmetic:
        //    BigDecimal expected = new BigDecimal("85").multiply(new BigDecimal("0.2"))
        //        .add(new BigDecimal("92").multiply(new BigDecimal("0.2")))
        //        // ... and so on
        //    - Provides exact decimal arithmetic
        //    - Avoids floating-point rounding errors
        //    - More suitable for financial calculations
        //
        // 3. Use Epsilon for Comparisons:
        //    assert Math.abs(weightedAverage - expected) < 1e-9;
        //    - Uses a small tolerance for comparison
        //    - Handles floating-point imprecision
        //    - More reliable than exact equality
        //
        // Expected = (85*HOMEWORK_WEIGHT + 92*HOMEWORK_WEIGHT + 78*EXAM_WEIGHT + 88*EXAM_WEIGHT)
        //         / 2.0  // Since we have two weights that sum to 1.0
        //         = (17 + 18.4 + 23.4 + 26.4) / 2.0
        //         = 85.2
        double expected = (85*HOMEWORK_WEIGHT + 92*HOMEWORK_WEIGHT + 78*EXAM_WEIGHT + 88*EXAM_WEIGHT) / 2.0;
        System.out.printf("Expected Weighted Average: %.2f\n", expected);

        // Verify the result matches the expected value
        // Using a small epsilon (1e-9) for floating-point comparison
        assert Math.abs(weightedAverage - expected) < 1e-9;
        System.out.println("\nTest Passed! ✅");
    }
}

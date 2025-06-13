package intermediate;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;

public class CustomSummaryStatisticsCollector {
    // Problem 5: Custom Summary Statistics Collector
    /**
     * A class to hold summary statistics for a collection of numbers.
     * Tracks count, sum, minimum, maximum, and can calculate average.
     */
    public static class Stats {
        int count;
        double sum;
        double min = Double.MAX_VALUE;  // Initialize to maximum possible value
        double max = Double.MIN_VALUE;  // Initialize to minimum possible value

        /**
         * Accumulates a single value into the statistics.
         * Updates count, sum, min, and max.
         */
        public void accept(double value) {
            count++;
            sum += value;
            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        /**
         * Combines this Stats object with another.
         * Used when merging statistics from parallel streams.
         */
        public void combine(Stats other) {
            count += other.count;
            sum += other.sum;
            min = Math.min(min, other.min);
            max = Math.max(max, other.max);
        }

        /**
         * Calculates the average of all values.
         * Returns 0 if no values have been accumulated.
         */
        public double getAvg() { return count == 0 ? 0 : sum / count; }
    }

    /**
     * Creates a custom collector that calculates summary statistics for a stream of numbers.
     * The collector uses the Stats class to accumulate:
     * - Count of numbers
     * - Sum of numbers
     * - Minimum value
     * - Maximum value
     * - Average value
     * 
     * @return A collector that produces a Stats object containing the summary statistics
     */
    public static Collector<Double, Stats, Stats> statsCollector() {
        return Collector.of(
                Stats::new,                                    // Supplier: creates new Stats object
                Stats::accept,                                 // Accumulator: adds a value to Stats
                (stats, stats2) -> {                          // Combiner: merges two Stats objects
                    stats.combine(stats2);
                    return stats;
                },
                Function.identity()                           // Finisher: returns the final Stats object
        );
    }

    public static void main(String[] args) {
        // Problem 5: Test the custom collector with sample data
        List<Double> values = List.of(10.0, 20.0, 30.0);
        Stats stats = values.stream().collect(statsCollector());
        
        // Expected output:
        // count=3 (3 numbers)
        // sum=60.0 (10.0 + 20.0 + 30.0)
        // avg=20.0 (60.0 / 3)
        // min=10.0 (smallest number)
        // max=30.0 (largest number)
        System.out.println("Problem 5: count=" + stats.count + 
                         ", sum=" + stats.sum + 
                         ", avg=" + stats.getAvg() + 
                         ", min=" + stats.min + 
                         ", max=" + stats.max);
    }
}

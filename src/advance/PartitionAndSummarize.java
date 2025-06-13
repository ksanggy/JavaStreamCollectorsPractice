package advance;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class PartitionAndSummarize {
    /**
     * Creates a collector that partitions elements based on a predicate and calculates
     * statistics for each partition using a custom Stats collector.
     * 
     * @param <T> The type of elements in the stream
     * @param predicate The predicate to partition elements (true/false)
     * @param mapper Function to convert elements to double values for statistics
     * @return A collector that produces a Map<Boolean, Stats> where:
     *         - Key (true): Statistics for elements matching the predicate
     *         - Key (false): Statistics for elements not matching the predicate
     */
    public static <T> Collector<T, ?, Map<Boolean, Stats>> partitioningAndSummarizing(
            Predicate<T> predicate,
            ToDoubleFunction<T> mapper) {
        return Collectors.partitioningBy(
                predicate,  // Partition elements into true/false based on predicate
                Collector.of(
                        Stats::new,  // Supplier: creates new Stats object for each partition
                        // Accumulator: converts element to double and adds to Stats
                        (stats, value) -> stats.accept(mapper.applyAsDouble(value)),
                        // Combiner: merges Stats objects when processing in parallel
                        (stats1, stats2) -> { stats1.combine(stats2); return stats1; },
                        // Finisher: returns the Stats object as is
                        Function.identity()
                )
        );
    }

    public static void main(String[] args) {
        // Test data: mix of integer and non-integer numbers
        List<Double> numbers = List.of(1.0, 2.5, 3.0, 4.5, 5.0, 6.5, 7.0, 8.5, 9.0, 10.5);

        // Test: Partition numbers into integers and non-integers and get stats for each
        Predicate<Double> isInteger = n -> n % 1 == 0;  // Check if number is integer
        ToDoubleFunction<Double> doubleValue = Double::doubleValue;  // Convert to double

        // Create and use the collector
        Collector<Double, ?, Map<Boolean, Stats>> collector = partitioningAndSummarizing(isInteger, doubleValue);
        Map<Boolean, Stats> statsByPartition = numbers.stream().collect(collector);

        // Print results
        System.out.println("Stats for numbers that are integers (true) and not (false):");
        statsByPartition.forEach((k, v) -> System.out.println(k + ": " + v));

        // Verification
        Stats integerStats = statsByPartition.get(true);    // Stats for integers (1.0, 3.0, 5.0, 7.0, 9.0)
        Stats nonIntegerStats = statsByPartition.get(false); // Stats for non-integers (2.5, 4.5, 6.5, 8.5, 10.5)

        // Verify expected results
        assert integerStats.getCount() == 4;      // 4 integer numbers
        assert integerStats.getSum() == 20.0;     // 1.0 + 3.0 + 5.0 + 7.0 + 9.0 = 20.0
        assert nonIntegerStats.getCount() == 6;   // 6 non-integer numbers
        assert nonIntegerStats.getSum() == 32.5;  // 2.5 + 4.5 + 6.5 + 8.5 + 10.5 = 32.5
        System.out.println("\nTest Passed! ✅");
    }
}

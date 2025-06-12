package beginner;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

// Beginner Problem 3: Total and Average Word Length
// Input: List<String> words
// Output: Custom object { totalLength, averageLength }
public class TotalAndAverageWordLength {
    @SuppressWarnings("all")
    public static void main( String[] args ) {
        try {
            LengthStats lengthStats = FileManager.readFile("test.txt", totalAndAverageWordLengthCollector());
            System.out.println("lengthStats = " + lengthStats);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a collector that calculates total and average word lengths.
     * Uses a custom collector with an array accumulator:
     * - acc[0] stores total length
     * - acc[1] stores word count
     * 
     * @return A collector that produces LengthStats containing total and average lengths
     */
    public static Collector<String, ?, LengthStats> totalAndAverageWordLengthCollector() {
        return Collectors.mapping(
            lineToWordsCollector(),
            Collectors.flatMapping(
                List::stream,
                Collector.of(
                    () -> new long[2],  // supplier: [totalLength, count]
                    (acc, word) -> {    // accumulator
                        acc[0] += word.length();
                        acc[1]++;
                    },
                    (acc1, acc2) -> {   // combiner
                        acc1[0] += acc2[0];
                        acc1[1] += acc2[1];
                        return acc1;
                    },
                    acc -> new LengthStats(  // finisher
                        (int) acc[0],
                        acc[1] == 0 ? 0 : (double) acc[0] / acc[1]
                    )
                )
            )
        );
    }

    /**
     * Immutable class that holds statistics about word lengths in a text.
     * Contains:
     * - totalLength: sum of all word lengths
     * - averageLength: mean length of all words
     * <a/>
     * Example:
     * For text "This is a test" (words: "this", "is", "a", "test")
     * - totalLength = 10 (4 + 2 + 1 + 3)
     * - averageLength = 2.5 (10 / 4)
     */
    public static class LengthStats {
        private final int totalLength;      // Total number of characters across all words
        private final double averageLength; // Mean length of all words (totalLength / number of words)

        /**
         * Creates a new LengthStats instance.
         * 
         * @param totalLength The sum of all word lengths
         * @param averageLength The mean length of all words
         */
        public LengthStats(int totalLength, double averageLength) {
            this.totalLength = totalLength;
            this.averageLength = averageLength;
        }

        @Override
        public String toString() {
            return "totalLength=" + totalLength + ", averageLength=" + averageLength;
        }
    }

    /**
     * Transforms a line of text into a list of processed words.
     * Processing pipeline:
     * 1. Trims leading/trailing whitespace
     * 2. Splits by single space
     * 3. Converts all words to lowercase
     * 4. Removes empty strings
     * 5. Keeps only alphabetic words (a-z)
     * <a/>
     * Example:
     * Input: "  This is a TEST!  "
     * Output: ["this", "is", "a", "test"]
     * 
     * @return A function that processes a line into a list of clean, alphabetic words
     */
    public static Function<String, List<String>> lineToWordsCollector() {
        return line -> Arrays.stream(line.trim().split(" "))
                .map(String::toLowerCase)
                .filter(word -> !word.isEmpty())
                .filter(word -> word.matches("[a-z]+"))
                .collect(Collectors.toList());
    }

}

package beginner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * A collector that groups words by their length and counts the frequency of each length.
 * This is useful for analyzing the distribution of word lengths in a text file.
 * <a/>
 * Beginner Problem 2: String Length Bins
 * Input: List<String> strings
 * Output: Map<Integer, Long> => string length -> count
 * <a/>
 *  Example output:
 * {1=1, 2=1, 4=2, 5=2} means:
 * - 1 word of length 1
 * - 1 word of length 2
 * - 2 words of length 4
 * - 2 words of length 5
 */
public class StringLengthBinCollector {

    @SuppressWarnings("all")
    public static void main( String[] args ) {
        try {
            Map<Integer, Long> result = FileManager.readFile("test.txt", stringLengthBinCollector());
            System.out.println("result = " + result);
        } catch (Exception e) {
            System.err.println("Error : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Transforms a line of text into a list of processed words.
     * Processing steps:
     * 1. Trims whitespace
     * 2. Splits by whitespace
     * 3. Converts to lowercase
     * 4. Removes empty strings
     * 5. Keeps only alphabetic words
     * 
     * @return A function that processes a line into a list of words
     */
    private static Function<String, List<String>> lineToWords() {
        return line -> Arrays.stream(line.trim().split("\\s+"))
                .map(String::toLowerCase)
                .filter(word -> !word.isEmpty())
                .filter(word -> word.matches("[a-z]+"))
                .collect(Collectors.toList());
    }

    /**
     * Creates a collector that groups words by their length and counts frequencies.
     * The collector pipeline:
     * 1. Maps each line to a list of words using lineToWords()
     * 2. Flattens all word lists into a single stream
     * 3. Groups words by their length
     * 4. Counts the frequency of each length
     * 
     * @return A collector that produces a Map<Integer, Long> where:
     *         - Key: word length
     *         - Value: number of words with that length
     */
    private static Collector<String, ?, Map<Integer, Long>> stringLengthBinCollector() {
        return Collectors.mapping(
            lineToWords(),
            Collectors.flatMapping(
                List::stream,
                Collectors.groupingBy(
                    String::length,
                    Collectors.counting()
                )
            )
        );
    }
}

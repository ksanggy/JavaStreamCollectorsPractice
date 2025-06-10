import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class FileWordFreqCollection {

    /**
     * Creates a collector that counts the frequency of each word in a stream.
     * <a/>
     * Function.identity() is used as the classifier function in groupingBy.
     * It's equivalent to writing (x) -> x, meaning "use the element itself as the key".
     * For example, if we have words ["this", "is", "this", "test"],
     * it will group them by the word itself, resulting in:
     * {"this": 2, "is": 1, "test": 1}
     * <a/>
     * @return A collector that produces a Map<String, Long> where:
     *         - Key: the word
     *         - Value: the count of occurrences
     */
    public static Collector<String, ?, Map<String, Long>> wordFreqCollector() {
        return Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
        );
    }

    @SuppressWarnings("all")
    public static void main( String[] args ) {
        try {
            Collector<String, ?, Map<String, Long>> wordProcessor = Collectors.mapping(
                    line -> Arrays.stream(line.split("\\s+"))  // Split by whitespace and flatten
                            .map(String::toLowerCase)  // Convert to lowercase for case-insensitive counting
                            .filter(word -> !word.isEmpty())  // Remove empty strings
                            .filter(word -> word.matches("[a-z]+"))  // Only keep words with alphabetic characters
                            .collect(Collectors.toList()
                            ),
                    Collectors.flatMapping(
                            list -> list.stream(),
                            wordFreqCollector()
                    )
            );
            System.out.println("Word frequencies: " + FileManager.readFile("test.txt", wordProcessor));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

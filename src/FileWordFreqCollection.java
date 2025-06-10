import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Arrays;

public class FileWordFreqCollection {

    public static Collector<String, ?, Map<String, Long>> wordFreqCollector() {
        return Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
        );
    }

    @SuppressWarnings("all")
    public static void main( String[] args ) {
        try {
            // Get the project root directory
            Path projectRoot = Paths.get(System.getProperty("user.dir"));
            Path testFile = projectRoot.resolve("test.txt");
            
            // Check if file exists
            if (!Files.exists(testFile)) {
                System.err.println("Error: test.txt not found at: " + testFile.toAbsolutePath());
                return;
            }
            Map<String, Long> wordFrequencies = Files.lines(testFile)
                .flatMap(line -> Arrays.stream(line.split("\\s+")))  // Split by whitespace and flatten
                .map(String::toLowerCase)  // Convert to lowercase for case-insensitive counting
                .filter(word -> !word.isEmpty())  // Remove empty strings
                .filter(word -> word.matches("[a-z]+"))  // Only keep words with alphabetic characters
                .collect(wordFreqCollector());
            
            System.out.println("Word frequencies: " + wordFrequencies);
            
        } catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

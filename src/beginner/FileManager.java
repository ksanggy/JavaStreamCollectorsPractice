package beginner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Utility class for file operations with support for custom stream collectors.
 * Provides reusable methods for file reading and processing.
 */
public class FileManager {

    /**
     * Reads a file and processes its contents using a custom collector.
     *
     * @param <R> The final result type of the collector
     * @param fileName The name of the file to read
     * @param collector The collector to use for processing the file contents
     * @return The result of applying the collector to the file contents
     * @throws RuntimeException if there's an error reading the file
     */
    public static <R> R readFile( String fileName, Collector<String, ?, R> collector) {
        try {
            Path projectRoot = Paths.get(System.getProperty("user.dir")); // get project root directory
            Path filePath = projectRoot.resolve(Paths.get(fileName));     // get file path

            if(!Files.exists(filePath)) {
                throw new RuntimeException("File not found: " + filePath.toAbsolutePath());
            }

            try (Stream<String> lines = Files.lines(filePath)) {
                return lines.collect(collector);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading file = " + e.getMessage(), e);
        }
    }
}

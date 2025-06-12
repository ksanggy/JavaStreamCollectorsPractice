package intermediate;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// Problem 1: Most Frequent Word by First Letter
// Input: List<String> words
// Output: Map<Character, String> => first letter -> most frequent word starting with that letter
// Example:
// Input: ["apple", "ant", "banana", "bat", "bat", "cat"]
// Output: {a=apple, b=bat, c=cat}
// Note: For letter 'b', "bat" is chosen because it appears twice
public class MostFrequentWordByFirstLetter {
    public static Map<Character, String> mostFrequentWordByFirstLetter(List<String> words) {
        return words.stream().collect(
                // First grouping: by first letter of each word
                Collectors.groupingBy(
                        word -> word.charAt(0),
                        // Second operation: find the most frequent word for each first letter
                        Collectors.collectingAndThen(
                                // Group words by the word itself and count occurrences
                                Collectors.groupingBy(
                                        Function.identity(),  // Use the word as the key
                                        Collectors.counting() // Count how many times each word appears
                                ),
                                // Find the word with the highest count
                                wordCount -> wordCount.entrySet().stream()
                                        .max(Map.Entry.comparingByValue())  // Get entry with max count
                                        .map(Map.Entry::getKey)            // Extract the word
                                        .orElse("")                        // Handle empty case
                        )
                )
        );
    }

    public static void main(String[] args) {
        // Problem 1
        List<String> words = List.of("apple", "ant", "banana", "bat", "bat", "cat");
        System.out.println("Problem 1: " + mostFrequentWordByFirstLetter(words));
    }
}

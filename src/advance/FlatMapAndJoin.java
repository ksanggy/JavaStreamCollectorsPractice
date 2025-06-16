package advance;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 7. Flattening, Mapping, and Joining with a Custom Delimiter
 * Problem: Given a list of Author objects, where each author has a list of Book objects,
 * create a single string that lists all books published after a certain year.
 * The string should be formatted as "Author Name: Book Title; Author Name: Book Title; ...".
 * That is, each entry is "Author: Title" and entries are separated by a semicolon and a space.
 */
public class FlatMapAndJoin {
    /**
     * Represents a book with its title and publication year.
     * Using a record for immutable data structure.
     */
    public record Book(String title, int publishYear) {}

    /**
     * Represents an author with their name and list of books.
     * Using a record for immutable data structure.
     */
    public record Author(String name, List<Book> books) {}

    /**
     * Lists all books published after a given year, formatted as "Author: Title" entries.
     * <p/>
     * Previous versions had these issues:
     * 1. First version:
     *    - Used Collectors.flatMapping incorrectly
     *    - Tried to use a combiner function that didn't match the types
     *    - Didn't maintain author information during flattening
     * <p/>
     * 2. Second version:
     *    - Created unnecessary intermediate List with toList()
     *    - Created a second stream from the filtered list
     *    - Used String.join instead of more readable String.format
     *    - Had incorrect year comparison (== instead of >)
     * <p/>
     * Improvements in current version:
     * 1. Efficiency:
     *    - No intermediate collections created
     *    - Direct chaining of stream operations
     *    - Single stream pipeline
     * <p/>
     * 2. Readability:
     *    - Clear string formatting with String.format
     *    - Each operation on its own line
     *    - Descriptive comments
     * <p/>
     * 3. Correctness:
     *    - Proper year comparison (book.publishYear > year)
     *    - Maintains author information throughout
     *    - Correct delimiter format
     *
     * @param authors List of authors with their books
     * @param year The year to filter books after
     * @return Formatted string of books published after the given year
     */
    public static String listBooksPublishedAfter(List<Author> authors, int year) {
        return authors.stream()
                // Flatten the nested structure of authors and their books
                // This creates a single stream of books while maintaining author context
                .flatMap(author -> author.books.stream()
                        // Filter books published after the given year
                        // Note: Using > instead of >= to match "after" requirement
                        .filter(book -> book.publishYear > year)
                        // Map each book to the desired format "Author: Title"
                        // Using String.format for better readability and maintainability
                        .map(book -> String.format("%s: %s", author.name, book.title))
                )
                // Join all entries with semicolon and space
                // This creates the final formatted string
                .collect(Collectors.joining("; "));
    }

    public static void main(String[] args) {
        // Test data with various publication years
        Author author1 = new Author("Jane Doe", List.of(
                new Book("The Beginning", 2018),  // Before 2020
                new Book("The Sequel", 2021)      // After 2020
        ));
        Author author2 = new Author("John Smith", List.of(
                new Book("Another Tale", 2019),   // Before 2020
                new Book("The Final Chapter", 2022) // After 2020
        ));
        List<Author> authors = List.of(author1, author2);

        String result = listBooksPublishedAfter(authors, 2020);
        System.out.println("Formatted Book List: " + result);

        // Verification
        String expected = "Jane Doe: The Sequel; John Smith: The Final Chapter";
        assert result.equals(expected);
        System.out.println("\nTest Passed! ✅");
    }
}

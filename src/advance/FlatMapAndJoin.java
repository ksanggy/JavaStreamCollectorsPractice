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
    public record Book(String title, int publishYear) {}
    public record Author(String name, List<Book> books) {}

    public static String listBooksPublishedAfter(List<Author> authors, int year) {
        // TODO: Implement using flatMap to get a stream of books from authors,
        // filter by year, map to the desired string format, and then join.
        // HINT: You might need an intermediate object or to flatMap in a way
        // that keeps the author's name alongside the book.
        return authors.stream().map(author -> {
            List<Book> filteredBookByPublishYear = author.books.stream().filter(book -> year <= book.publishYear).toList();
            return filteredBookByPublishYear.stream().map(book -> String.join(":", author.name, book.title));
        }).flatMap(stream -> stream).collect(Collectors.joining("; "));
    }

    public static void main(String[] args) {
        Author author1 = new Author("Jane Doe", List.of(
                new Book("The Beginning", 2018),
                new Book("The Sequel", 2021)
        ));
        Author author2 = new Author("John Smith", List.of(
                new Book("Another Tale", 2019),
                new Book("The Final Chapter", 2022)
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

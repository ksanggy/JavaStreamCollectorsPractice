package advance;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

/**
 * 5. collectingAndThen for an Immutable, Sorted, Reversed List
 * Problem: Given a list of integers, create a stream pipeline that first filters for odd numbers,
 * then collects them into a list, sorts them in natural order, and finally wraps the result in an unmodifiable,
 * reversed view of that list. The key is to perform the sorting and unmodifying actions after the initial collection.
 */
public class CollectingAndThenImmutableView {
    /**
     * Creates an unmodifiable, reversed, sorted list of odd numbers from the input list.
     * The method:
     * 1. Filters for odd numbers
     * 2. Collects them into a list
     * 3. Sorts them in natural order
     * 4. Creates an unmodifiable, reversed view
     *
     * This is the more efficient implementation because:
     * - Creates only one modifiable list
     * - Sorts that list in-place (no new list created)
     * - Creates a single reversed view
     * - Wraps that view in an unmodifiable wrapper
     *
     * @param numbers Input list of integers
     * @return Unmodifiable, reversed, sorted list of odd numbers
     */
    public static List<Integer> getSortedReversedUnmodifiableOddNumbers(List<Integer> numbers) {
        return numbers.stream()
                .filter(n -> n % 2 != 0)  // Filter odd numbers
                .collect(
                        Collectors.collectingAndThen(
                                // First collect to a modifiable list for sorting
                                Collectors.toList(),
                                // Then sort, reverse, and make unmodifiable
                                list -> {
                                    list.sort(Integer::compareTo);  // Sort in natural order
                                    return Collections.unmodifiableList(list.reversed());  // Create unmodifiable reversed view
                                }
                        )
                );
    }

    /**
     * Alternative implementation that creates multiple intermediate objects.
     * This approach is less efficient because it:
     * - Creates an unmodifiable list first (which then needs to be converted back to modifiable)
     * - Creates a new stream from the collected list
     * - Creates a new sorted list
     * - Creates a final list
     * - Creates a reversed view
     *
     * @param numbers Input list of integers
     * @return Unmodifiable, reversed, sorted list of odd numbers
     */
    public static List<Integer> getSortedReversedUnmodifiableOddNumbersAlternative(List<Integer> numbers) {
        return numbers.stream()
                .filter(n -> n % 2 != 0)
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.toUnmodifiableList(),  // Creates first list
                                integers -> integers.stream()      // Creates new stream
                                        .sorted()                  // Creates second list
                                        .toList()                  // Creates third list
                                        .reversed()                // Creates view
                        )
                );
    }

    public static void main(String[] args) {
        List<Integer> numbers = List.of(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);

        // Test the efficient implementation
        List<Integer> result = getSortedReversedUnmodifiableOddNumbers(numbers);
        System.out.println("Resulting list (efficient): " + result);

        // Test the alternative implementation
        List<Integer> resultAlt = getSortedReversedUnmodifiableOddNumbersAlternative(numbers);
        System.out.println("Resulting list (alternative): " + resultAlt);

        // Verification
        List<Integer> expected = List.of(9, 5, 5, 5, 3, 3, 1, 1);
        assert result.equals(expected);
        assert resultAlt.equals(expected);

        // Verify unmodifiability
        try {
            result.add(100);
            // If this line is reached, the test fails.
            System.err.println("Test Failed: List is modifiable.");
        } catch (UnsupportedOperationException e) {
            System.out.println("Successfully caught UnsupportedOperationException. The list is unmodifiable.");
            System.out.println("\nTest Passed! ✅");
        }
    }
}

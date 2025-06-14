package advance;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 4. teeing Collector for Min/Max Gap
 * Problem: Given a list of Event objects, each with a timestamp,
 * use Collectors.teeing() to find both the earliest and latest event in a single stream operation.
 * Then, calculate the Duration between them. Return the result as a Duration object.
 */
public class TeeingForMinMax {
    /**
     * Represents an event with an ID and timestamp.
     * Using a record for immutable data structure.
     */
    public record Event(String id, Instant timestamp) {}

    /**
     * Finds the time gap between the earliest and latest events in the list.
     * Uses Collectors.teeing to efficiently find both min and max in a single pass.
     *
     * @param events List of events to analyze
     * @return Duration between earliest and latest event
     * @throws IllegalArgumentException if the events list is empty
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static Duration findEventTimeGap(List<Event> events) {
        if (events.isEmpty()) {
            throw new IllegalArgumentException("Cannot find time gap in empty event list");
        }

        return events.stream().collect(
                Collectors.teeing(
                        // First collector: Find event with minimum timestamp
                        Collectors.minBy(Comparator.comparing(Event::timestamp)),
                        // Second collector: Find event with maximum timestamp
                        Collectors.maxBy(Comparator.comparing(Event::timestamp)),
                        // Merger function: Calculate duration between min and max timestamps
                        // We can safely use get() here because:
                        // 1. We checked for empty list above
                        // 2. minBy and maxBy will always return a value for non-empty lists
                        (minEvent, maxEvent) -> Duration.between(
                                minEvent.get().timestamp(),
                                maxEvent.get().timestamp()
                        )
                )
        );
    }

    public static void main(String[] args) {
        List<Event> events = List.of(
                new Event("E1", Instant.parse("2023-01-01T10:00:00Z")),
                new Event("E2", Instant.parse("2023-01-01T10:15:00Z")),
                new Event("E3", Instant.parse("2023-01-01T09:45:00Z")), // Earliest
                new Event("E4", Instant.parse("2023-01-01T11:00:00Z")), // Latest
                new Event("E5", Instant.parse("2023-01-01T10:30:00Z"))
        );

        Duration gap = findEventTimeGap(events);
        System.out.println("Time gap between earliest and latest event: " + gap);

        // Verification
        assert gap.equals(Duration.ofMinutes(75));
        System.out.println("\nTest Passed! ✅");

        // Test empty list handling
        try {
            findEventTimeGap(List.of());
            assert false : "Should have thrown IllegalArgumentException";
        } catch (IllegalArgumentException e) {
            System.out.println("Empty list test passed: " + e.getMessage());
        }
    }
}

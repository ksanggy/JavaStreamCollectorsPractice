package advance;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TeeingForMinMax {
    public record Event(String id, Instant timestamp) {}

    public static Duration findEventTimeGap(List<Event> events) {
        // TODO: Implement using Collectors.teeing.
        // The first downstream collector should find the min timestamp.
        // The second downstream collector should find the max timestamp.
        // The merger function will calculate the duration between them.
        return events.stream().collect(
                Collectors.teeing(
                        Collectors.minBy(Comparator.comparing(Event::timestamp)),
                        Collectors.maxBy(Comparator.comparing(Event::timestamp)),
                        (event, event2) -> Duration.between(event.get().timestamp(), event2.get().timestamp())
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
    }
}

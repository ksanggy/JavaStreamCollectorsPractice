package advance;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 6. Concurrent Map with a Complex Merge Function
 * Problem: You have a stream of Update objects, each containing a key, a value, and an operation (SET or APPEND).
 * Collect these into a ConcurrentMap<String, String>.
 * <p>
 * If a key appears for the first time, its value is set.
 * If a key already exists and the new operation is SET, the old value is overwritten.
 * If a key already exists and the new operation is APPEND, the new value is appended to the old value,
 * separated by a comma.
 */
public class ConcurrentMapWithMerge {
    enum Operation { SET, APPEND }
    public record Update(String key, String value, Operation op) {}

    public static ConcurrentMap<String, String> processUpdates(List<Update> updates) {
        return updates.stream().collect(Collectors.toConcurrentMap(
                Update::key,
                // For the first value, encode operation with value
                d -> String.join(" ", d.op.toString(), d.value),
                // Merge function handles both cases:
                // 1. When first value is a plain value (from previous merge)
                // 2. When first value contains operation info (from initial insert)
                (value1, value2) -> {
                    String[] parts2 = value2.split(" ");
                    String operation = parts2[0];
                    String newValue = parts2[1];

                    // Check if value1 contains operation info
                    if (value1.contains(" ")) {
                        // value1 has operation info, check new operation
                        if (Operation.SET.toString().equals(operation)) {
                            // For SET, return new value with operation
                            return String.join(" ", operation, newValue);
                        } else {
                            // For APPEND, concatenate values and keep APPEND operation
                            String[] parts1 = value1.split(" ");
                            return String.join(" ", Operation.APPEND.toString(), 
                                    parts1[1] + "," + newValue);
                        }
                    } else {
                        // value1 is a plain value, check new operation
                        if (Operation.SET.toString().equals(operation)) {
                            // For SET, return new value with operation
                            return String.join(" ", operation, newValue);
                        } else {
                            // For APPEND, concatenate values and keep APPEND operation
                            return String.join(" ", Operation.APPEND.toString(), 
                                    value1 + "," + newValue);
                        }
                    }
                }
        ));
    }

    public static void main(String[] args) {
        List<Update> updates = List.of(
                new Update("A", "val1", Operation.SET),
                new Update("B", "valB1", Operation.SET),
                new Update("A", "val2", Operation.APPEND), // A -> "val1,val2"
                new Update("C", "valC1", Operation.SET),
                new Update("B", "valB2", Operation.SET),   // B -> "valB2" (overwrite)
                new Update("A", "val3", Operation.APPEND)  // A -> "val1,val2,val3"
        );

        ConcurrentMap<String, String> result = processUpdates(updates);
        System.out.println("Merged Map State: " + result);

        // Verification
        assert result.get("A").equals("val1,val2,val3");
        assert result.get("B").equals("valB2");
        assert result.get("C").equals("valC1");
        System.out.println("\nTest Passed! ✅");
    }
}

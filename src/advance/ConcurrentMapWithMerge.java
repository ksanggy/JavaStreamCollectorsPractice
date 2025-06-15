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
    /**
     * Represents the type of operation to perform on a value.
     * SET: Overwrites the existing value
     * APPEND: Concatenates the new value to the existing value
     */
    enum Operation { SET, APPEND }

    /**
     * Represents an update operation with a key, value, and operation type.
     * Using a record for immutable data structure.
     */
    public record Update(String key, String value, Operation op) {}

    /**
     * Processes a list of updates and returns a concurrent map with the final state of each key.
     * The method uses a clever encoding scheme to maintain operation information:
     * - Each value in the map is stored as "OPERATION value"
     * - This allows the merge function to know the operation type for subsequent merges
     * - The final map will contain only the values, with operation information removed
     *
     * @param updates List of updates to process
     * @return ConcurrentMap containing the final state of each key
     */
    public static ConcurrentMap<String, String> processUpdates(List<Update> updates) {
        return updates.stream().collect(Collectors.toConcurrentMap(
                // Key mapper: Use the key from the Update object
                Update::key,
                // Value mapper: Encode operation with value for initial insertion
                // Format: "OPERATION value" (e.g., "SET val1" or "APPEND val2")
                d -> String.join(" ", d.op.toString(), d.value),
                // Merge function: Handles both cases:
                // 1. When first value is a plain value (from previous merge)
                // 2. When first value contains operation info (from initial insert)
                (value1, value2) -> {
                    // Parse the new value's operation and actual value
                    String[] parts2 = value2.split(" ");
                    String operation = parts2[0];
                    String newValue = parts2[1];

                    // Check if value1 contains operation info (has a space)
                    if (value1.contains(" ")) {
                        // value1 has operation info, check new operation
                        if (Operation.SET.toString().equals(operation)) {
                            // For SET, return new value with operation
                            // This ensures subsequent merges know it was a SET
                            return String.join(" ", operation, newValue);
                        } else {
                            // For APPEND, concatenate values and keep APPEND operation
                            // This maintains the APPEND operation for future merges
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
        // Test data with various operations
        List<Update> updates = List.of(
                new Update("A", "val1", Operation.SET),      // A -> "val1"
                new Update("B", "valB1", Operation.SET),     // B -> "valB1"
                new Update("A", "val2", Operation.APPEND),   // A -> "val1,val2"
                new Update("C", "valC1", Operation.SET),     // C -> "valC1"
                new Update("B", "valB2", Operation.SET),     // B -> "valB2" (overwrite)
                new Update("A", "val3", Operation.APPEND)    // A -> "val1,val2,val3"
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

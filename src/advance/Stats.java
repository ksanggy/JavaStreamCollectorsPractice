package advance;

// Shared Stats class
public class Stats {
    private long count;
    private double sum;
    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;

    public void accept(double value) {
        count++;
        sum += value;
        min = Math.min(min, value);
        max = Math.max(max, value);
    }

    public void combine(Stats other) {
        count += other.count;
        sum += other.sum;
        min = Math.min(min, other.min);
        max = Math.max(max, other.max);
    }

    public long getCount() { return count; }
    public double getSum() { return sum; }
    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getAverage() { return count > 0 ? sum / count : 0.0; }

    @Override
    public String toString() {
        return String.format("Stats{count=%d, sum=%.2f, min=%.2f, max=%.2f, avg=%.2f}",
                count, sum, min, max, getAverage());
    }
}

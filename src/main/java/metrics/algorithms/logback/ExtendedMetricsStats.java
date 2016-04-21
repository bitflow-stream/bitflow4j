package metrics.algorithms.logback;

/**
 * Created by anton on 4/21/16.
 */
public class ExtendedMetricsStats extends MetricStatistics {

    public double min = Double.MAX_VALUE;
    public double max = Double.MIN_VALUE;

    public ExtendedMetricsStats(String name) {
        super(name);
    }

    @Override
    public void add(double val) {
        super.add(val);
        if (!Double.isNaN(val)) {
            min = Double.min(min, val);
            max = Double.max(max, val);
        }
    }

}

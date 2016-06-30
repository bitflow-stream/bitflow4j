package metrics.io.window;

/**
 * Created by anton on 4/21/16.
 */
public class MetricStatisticsWindow extends MetricWindow {

    public static final MetricWindowFactory<MetricStatisticsWindow> FACTORY = MetricStatisticsWindow::new;

    public double sum = 0;

    // These are reset with clear(), but not fixed in flushSamples()
    public double totalMinimum = Double.MAX_VALUE;
    public double totalMaximum = Double.MIN_VALUE;

    public MetricStatisticsWindow(String name) {
        super(name);
    }

    @Override
    public void flushSamples(int numSamples) {
        for (int i = 0; i < numSamples; i++) {
            if (i >= values.size()) break;
            double val = values.get(i);
            sum -= val;
        }
        super.flushSamples(numSamples);
    }

    @Override
    public void addImpl(double val) {
        super.addImpl(val);
        sum += val;
        totalMinimum = Double.min(totalMinimum, val);
        totalMaximum = Double.max(totalMaximum, val);
    }

    public double start() {
        if (values.isEmpty()) return 0;
        return values.get(0);
    }

    public double end() {
        if (values.isEmpty()) return 0;
        return values.get(values.size() - 1);
    }

    public double slope() {
        if (values.isEmpty()) return 0;
        return values.get(values.size() - 1) - values.get(0);
    }

    public double average() {
        if (values.isEmpty()) return 0.0;
        return sum / values.size();
    }

    public double variance() {
        double avg = average();
        long size = values.size();
        double stdOffsetSum = 0.0;
        for (int i = 0; i < size; i++) {
            double val = values.get(i);
            if (!Double.isNaN(val)) {
                double offset = avg - val;
                double stdOffset = offset * offset;
                stdOffsetSum += stdOffset / size;
            }
        }
        return stdOffsetSum;
    }

    public double stdDeviation() {
        return Math.sqrt(variance());
    }

    // This is the coefficient of variation
    public double normalizedStdDeviation() {
        double avg = average();
        double dev = stdDeviation();
        double norm = avg == 0 ? dev : dev / avg;
        return Math.abs(norm);
    }

    @Override
    public void clear() {
        super.clear();
        sum = 0;
        totalMinimum = Double.MAX_VALUE;
        totalMaximum = Double.MIN_VALUE;
    }
}

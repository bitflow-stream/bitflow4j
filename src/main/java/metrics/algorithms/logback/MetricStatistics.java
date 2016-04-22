package metrics.algorithms.logback;

/**
 * Created by anton on 4/21/16.
 */
public class MetricStatistics extends MetricLog {

    public double sum = 0;
    public int realSize = 0;

    public MetricStatistics(String name) {
        super(name);
    }

    @Override
    public void add(double val) {
        super.add(val);
        if (!Double.isNaN(val)) {
            sum += val;
            realSize++;
        }
    }

    public double average() {
        if (realSize == 0) return 0.0;
        return sum / realSize;
    }

    public double variance() {
        double avg = average();
        long size = realSize;
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

}

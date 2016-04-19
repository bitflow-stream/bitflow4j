package metrics.algorithms;

/**
 * Created by anton on 4/18/16.
 */
public class FeatureStandardizer extends AbstractFeatureScaler {

    @Override
    public String toString() {
        return "feature standardization";
    }

    @Override
    protected MetricScaler createScaler(ExtendedMetricsStats stats) {
        return new MetricStandardizer(stats);
    }

    private static class MetricStandardizer implements MetricScaler {
        private final double stdDeviation;
        private final double average;

        MetricStandardizer(ExtendedMetricsStats stats) {
            average = stats.average();
            double stdDev = stats.stdDeviation();
            if (stdDev == 0) stdDev = 1;
            stdDeviation = stdDev;
        }

        public double scale(double val) {
            return (val - average) / stdDeviation;
        }
    }

}

package metrics.algorithms;

/**
 * Created by anton on 4/18/16.
 */
public class FeatureMinMaxScaler extends AbstractFeatureScaler {

    @Override
    public String toString() {
        return "min-max scaling";
    }

    @Override
    protected MetricScaler createScaler(ExtendedMetricsStats stats) {
        return new MetricMinMaxScaler(stats);
    }

    private static class MetricMinMaxScaler implements MetricScaler {
        private final double min;
        private final double max;

        MetricMinMaxScaler(ExtendedMetricsStats stats) {
            if (stats.min < stats.max) {
                min = stats.min;
                max = stats.max;
            } else {
                min = max = 0;
            }
        }

        public double scale(double val) {
            double range = max - min;
            if (range == 0)
                return val;
            return (val - min) / range;
        }

    }

}

package metrics.algorithms;

import metrics.io.window.MetricStatisticsWindow;

/**
 * Created by anton on 4/18/16.
 */
public class FeatureMinMaxScaler extends AbstractFeatureScaler {

    @Override
    public String toString() {
        return "min-max scaling";
    }

    @Override
    protected MetricScaler createScaler(MetricStatisticsWindow stats) {
        return new MetricMinMaxScaler(stats);
    }

    private static class MetricMinMaxScaler implements MetricScaler {
        private final double min;
        private final double max;

        MetricMinMaxScaler(MetricStatisticsWindow stats) {
            if (stats.totalMinimum < stats.totalMaximum) {
                min = stats.totalMinimum;
                max = stats.totalMaximum;
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

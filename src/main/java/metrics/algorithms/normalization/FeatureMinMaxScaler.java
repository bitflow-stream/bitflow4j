package metrics.algorithms.normalization;

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

    private static class MetricMinMaxScaler extends AbstractMetricScaler {
        MetricMinMaxScaler(MetricStatisticsWindow stats) {
            super(stats);
        }

        public double scale(double val) {
            double range = max - min;
            if (range == 0)
                return val;
            return (val - min) / range;
        }
    }

}

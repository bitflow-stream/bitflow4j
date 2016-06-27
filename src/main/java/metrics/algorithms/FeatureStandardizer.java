package metrics.algorithms;

import metrics.io.window.MetricStatisticsWindow;

/**
 * Created by anton on 4/18/16.
 */
public class FeatureStandardizer extends AbstractFeatureScaler {

    @Override
    public String toString() {
        return "feature standardization";
    }

    @Override
    protected MetricScaler createScaler(MetricStatisticsWindow stats) {
        return new MetricStandardizer(stats);
    }

    public static class MetricStandardizer extends AbstractMetricScaler {
        MetricStandardizer(MetricStatisticsWindow stats) {
            super(stats);
        }

        public double scale(double val) {
            return (val - average) / stdDeviation;
        }
    }

}

package bitflow4j.steps.normalization;

import bitflow4j.window.MetricStatisticsWindow;

/**
 * Created by anton on 4/18/16.
 */
public class FeatureMinMaxScaler extends AbstractFeatureScaler {

    @Override
    protected MetricScaler createScaler(MetricStatisticsWindow stats) {
        return new MetricMinMaxScaler(stats);
    }

    private static class MetricMinMaxScaler extends AbstractMetricScaler {

        MetricMinMaxScaler(MetricStatisticsWindow stats) {
            super(stats);
        }

        @Override
        public double scale(double val) {
            double range = max - min;
            if (range == 0) {
                return val;
            }
            return (val - min) / range;
        }
    }

}

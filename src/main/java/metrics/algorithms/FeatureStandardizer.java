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

    public static class MetricStandardizer implements MetricScaler {
        public final double stdDeviation;
        public final double average;

        MetricStandardizer(MetricStatisticsWindow stats) {
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

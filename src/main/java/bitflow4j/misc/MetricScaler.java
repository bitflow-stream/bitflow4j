package bitflow4j.misc;

import bitflow4j.Sample;

import java.util.List;

public interface MetricScaler {

    double scale(double val);

    abstract class AbstractMetricScaler implements MetricScaler {

        public final OnlineStatistics stats;

        public AbstractMetricScaler(OnlineStatistics stats) {
            this.stats = stats;
        }

        public AbstractMetricScaler(List<Sample> samples, int metricIndex) {
            this(OnlineStatistics.buildFrom(samples, metricIndex));
        }
    }

    class MinMaxScaler extends AbstractMetricScaler {

        public MinMaxScaler(OnlineStatistics stats) {
            super(stats);
        }

        public MinMaxScaler(List<Sample> samples, int metricIndex) {
            super(samples, metricIndex);
        }

        @Override
        public double scale(double val) {
            double range = stats.max - stats.min;
            if (range == 0) {
                return 0.5;
            }
            return (val - stats.min) / range;
        }
    }

    class MetricStandardizer extends AbstractMetricScaler {

        public MetricStandardizer(OnlineStatistics stats) {
            super(stats);
        }

        public MetricStandardizer(List<Sample> samples, int metricIndex) {
            super(samples, metricIndex);
        }

        public double scale(double val) {
            return (val - stats.mean()) / stats.standardDeviation();
        }
    }

}

package metrics.algorithms;

import gnu.trove.list.TDoubleList;
import metrics.Sample;
import metrics.algorithms.logback.LogbackAlgorithm;
import metrics.algorithms.logback.MetricLog;

/**
 * Created by anton on 4/21/16.
 */
public class FeatureAggregator extends LogbackAlgorithm<MetricLog> {

    private final Aggregation[] aggregations;

    public FeatureAggregator(Aggregation ...aggregations) {
        this.aggregations = aggregations;
    }

    public Sample executeSample(Sample sample) {
        // TODO for every metric, call every aggregation.
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public String toString() {
        String result = "feature aggregator [";
        for (int i = 0; i < aggregations.length; i++) {
            if (i > 0) result += ", ";
            result += aggregations[i].toString();
        }
        return result;
    }

    @Override
    protected MetricLog createMetricStats(String name) {
        return new MetricLog(name);
    }

    public static abstract class Aggregation {

        public final long seconds;
        public final String name;

        public Aggregation(String name, long seconds) {
            this.seconds = seconds;
            this.name = name;
        }

        public String toString() {
            return name;
        }

        /** Values will only contain the values from the last
         * {@link #seconds} seconds.
         */
        abstract double aggregate(TDoubleList values);

    }

}

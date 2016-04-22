package metrics.algorithms;

import metrics.Sample;
import metrics.algorithms.logback.LogbackAlgorithm;
import metrics.algorithms.logback.MetricLog;
import metrics.algorithms.logback.NoNanMetricLog;

/**
 * Created by anton on 4/21/16.
 */
public class FeatureAggregator extends LogbackAlgorithm<FeatureAggregator.AverageAggregatingLog> {

    public Sample executeSample(Sample sample) {
        String headerFields[] = new String[metrics.size() * 2];
        int i = 0;
        for (MetricLog stat : metrics.values()) {
            headerFields[i++] = stat.name;
            headerFields[i++] = stat.name + "_aggregated";
        }
        Sample.Header header = new Sample.Header(headerFields, Sample.Header.TOTAL_SPECIAL_FIELDS);

        // Call every aggregation for every metric
        double values[] = new double[headerFields.length];

        System.out.println("Aggregating sample nr " + samples.size());

        i = 0;
        for (AverageAggregatingLog metric : metrics.values()) {
            values[i++] = metric.getLatestValue();
            double aggregatedValues[] = metric.aggregateAll();
            for (double agg : aggregatedValues) {
                values[i++] = agg;
            }
        }
        return new Sample(header, values, sample.getTimestamp(), sample.getSource(), sample.getLabel());
    }

    @Override
    public String toString() {
        return "feature aggregator";
    }

    @Override
    protected AverageAggregatingLog createMetricStats(String name) {
        return new AverageAggregatingLog(name, new long[]{10}, this);
    }

    public abstract static class AggregatingLog extends NoNanMetricLog {

        final long intervals[];
        final LogbackAlgorithm<?> algo;

        public AggregatingLog(String name, long intervals[], LogbackAlgorithm<?> algo) {
            super(name);
            this.intervals = intervals;
            this.algo = algo;
        }

        public double[] aggregateAll() {
            double result[] = new double[intervals.length];
            for (int i = 0; i < result.length; i++) {
                long interval = intervals[i];
                int num = algo.countLatestSamples(interval);
                double aggregated = aggregate(num);
                result[i] = aggregated;
            }
            return result;
        }

        protected abstract double aggregate(int numSamples);

    }

    public static class AverageAggregatingLog extends AggregatingLog {

        public AverageAggregatingLog(String name, long[] intervals, LogbackAlgorithm<?> algo) {
            super(name, intervals, algo);
        }

        @Override
        protected double aggregate(int numSamples) {
            if (values.isEmpty()) {
                return 0;
            }

            System.out.println("==== average over: " + numSamples);

            double result = 0;
            int size = values.size();
            for (int i = size - 1; i >= 0 && i >= size - numSamples; i--) {
                result += values.get(i);
            }
            return result / numSamples;
        }
    }

}

package metrics.algorithms;

import gnu.trove.list.TDoubleList;
import metrics.Sample;
import metrics.algorithms.logback.LogbackAlgorithm;
import metrics.algorithms.logback.MetricLog;

import java.util.Arrays;

/**
 * Created by anton on 4/21/16.
 */
public class FeatureAggregator extends LogbackAlgorithm<MetricLog> {

    private final Aggregation[] aggregations;
    private final long[] intervals;
    private final long flushSeconds;

    public FeatureAggregator(long intervals[], Aggregation aggregations[]) {
        this.aggregations = aggregations;
        this.intervals = intervals;
        Arrays.sort(this.intervals); // ascending
        this.flushSeconds = this.intervals[this.intervals.length];
    }

    public Sample executeSample(Sample sample) {
        flushOldSamples(flushSeconds);

        String headerFields[] = new String[metrics.size() * (1 + intervals.length * aggregations.length)];
        int i = 0;
        for (MetricLog stat : metrics.values()) {
            headerFields[i++] = stat.name;
            for (long interval : intervals) {
                for (Aggregation agg : aggregations) {
                    headerFields[i++] = agg.headerSuffix(interval);
                }
            }
        }
        Sample.Header header = new Sample.Header(headerFields, Sample.Header.TOTAL_SPECIAL_FIELDS);

        // Count how many samples are in each interval
        int numSamples[] = new int[intervals.length];
        for (i = 0; i < numSamples.length; i++) {
            numSamples[i] = countLatestSamples(intervals[i]);
        }

        // Call every aggregation for every metric
        double values[] = new double[headerFields.length];

        i = 0;
        for (MetricLog metric : metrics.values()) {
            values[i++] = metric.getLatestValue();
            for (long interval : intervals) {
                for (Aggregation agg : aggregations) {
                    agg.aggregate(metric.getLatestValues(interval));
                }
            }
        }
        return new Sample(header, values, sample.getTimestamp(), sample.getSource(), sample.getLabel());
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

        public final String name;

        public Aggregation(String name) {
            this.name = name;
        }

        private String headerSuffix(long interval) {
            return "-" + toString() + "-" + interval;
        }

        public String toString() {
            return name;
        }

        /**
         * Values will only contain the sub-range of values to aggregate.
         */
        public abstract double aggregate(TDoubleList values);

    }

}

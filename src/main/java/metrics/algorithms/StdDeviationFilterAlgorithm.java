package metrics.algorithms;

import metrics.Sample;
import metrics.io.MetricOutputStream;

import java.io.IOException;
import java.util.*;

/**
 * Created by anton on 4/7/16.
 */
public class StdDeviationFilterAlgorithm extends PostAnalysisAlgorithm<StdDeviationFilterAlgorithm.Statistics> {

    private final double minNormalizedDeviation;

    public StdDeviationFilterAlgorithm(double minNormalizedDeviation) {
        super("stdDeviation-filter algorithm");
        this.minNormalizedDeviation = minNormalizedDeviation;
    }

    @Override
    protected Statistics createMetricStats(String name) {
        return new Statistics(name);
    }

    @Override
    protected void analyseSample(Sample sample) throws IOException {
        sample.checkConsistency();
        String[] header = sample.getHeader().header;
        Set<String> unhandledStats = new HashSet<>(metrics.keySet());
        double[] values = sample.getMetrics();
        for (int i = 0; i < header.length; i++) {
            Statistics stats = getStats(header[i]);
            stats.add(values[i]);
            unhandledStats.remove(stats.name); // Might not be contained
        }
        registerSample(sample);
        for (String unhandledHeader : unhandledStats) {
            metrics.get(unhandledHeader).fill(1);
        }
    }

    @Override
    protected void writeResults(MetricOutputStream output) throws IOException {
        double avgDeviation = 0;

        // Filter out low-deviation metrics
        List<Statistics> validStats = new ArrayList<>();

        for (Map.Entry<String, Statistics> metric : metrics.entrySet()) {
            Statistics stats = metric.getValue();
            double stdDeviation = stats.normalizedStdDeviation();
            if (stdDeviation > minNormalizedDeviation) {
                validStats.add(stats);
                avgDeviation += stdDeviation;
            }
        }
        if (validStats.isEmpty()) {
            System.err.println(getName() + " produced no output");
            return;
        }
        avgDeviation /= validStats.size();

        // Construct combined header
        String headerFields[] = new String[validStats.size()];
        for (int i = 0; i < headerFields.length; i++) {
            headerFields[i] = validStats.get(i).name;
        }
        Sample.Header header = new Sample.Header(headerFields, Sample.Header.TOTAL_SPECIAL_FIELDS);

        // Construct samples from remaining metrics
        for (int sampleNr = 0; sampleNr < samples.size(); sampleNr++) {
            double metrics[] = new double[headerFields.length];
            for (int metricNr = 0; metricNr < headerFields.length; metricNr++) {
                metrics[metricNr] = validStats.get(metricNr).getValue(sampleNr);
            }
            SampleMetadata meta = samples.get(sampleNr);
            Sample sample = new Sample(header, metrics, meta.timestamp, meta.source, meta.label);
            output.writeSample(sample);
        }

        System.err.printf("%d of %d metrics passed stdDeviation filter (%d filtered out). Avg normalized deviation: %f.\n",
                validStats.size(), metrics.size(), metrics.size() - validStats.size(), avgDeviation);
    }

    static class Statistics extends PostAnalysisAlgorithm.Metric {

        double sum = 0;

        Statistics(String name) {
            super(name);
        }

        @Override
        void add(double val) {
            super.add(val);
            if (!Double.isNaN(val)) {
                sum += val;
            }
        }

        double average() {
            if (realSize == 0) return 0.0;
            return sum / realSize;
        }

        double variance() {
            double avg = average();
            long size = realSize;
            double stdOffsetSum = 0.0;
            for (int i = 0; i < size; i++) {
                double val = list.get(i);
                if (!Double.isNaN(val)) {
                    double offset = avg - val;
                    double stdOffset = offset*offset;
                    stdOffsetSum += stdOffset / size;
                }
            }
            return stdOffsetSum;
        }

        double stdDeviation() {
            return Math.sqrt(variance());
        }

        // This is the coefficient of variation
        double normalizedStdDeviation() {
            double avg = average();
            double dev = stdDeviation();
            double norm = avg == 0 ? dev : dev / avg;
            return Math.abs(norm);
        }

    }

}

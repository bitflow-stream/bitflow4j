package metrics.algorithms;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import metrics.Sample;
import metrics.io.MetricOutputStream;

import java.io.IOException;
import java.util.*;

/**
 * Created by anton on 4/7/16.
 */
public class VarianceFilterAlgorithm extends PostAnalysisAlgorithm {

    private final double minVariance;
    private SortedMap<String, Statistics> stats = new TreeMap<>();
    private List<SampleMetadata> samples = new ArrayList<>();

    public VarianceFilterAlgorithm(double minVariance) {
        super("variance-filter algorithm");
        this.minVariance = minVariance;
    }

    private Statistics getStats(String name) {
        Statistics result;
        if ((result = stats.get(name)) == null) {
            result = new Statistics(name);
            result.fill(samples.size()); // Fill up missed values
            stats.put(name, result);
        }
        return result;
    }

    @Override
    protected void analyseSample(Sample sample) throws IOException {
        sample.checkConsistency();
        String[] header = sample.getHeader().header;
        Set<String> unhandledStats = new HashSet<>(stats.keySet());
        double[] values = sample.getMetrics();
        for (int i = 0; i < header.length; i++) {
            Statistics stats = getStats(header[i]);
            stats.add(values[i]);
            unhandledStats.remove(stats.name); // Might not be contained
        }
        samples.add(new SampleMetadata(sample.getTimestamp(), sample.getSource(), sample.getLabel()));
        for (String unhandledHeader : unhandledStats) {
            stats.get(unhandledHeader).fill(1);
        }
    }

    @Override
    protected void writeResults(MetricOutputStream output) throws IOException {
        // Filter out low-variance metrics
        List<Statistics> validStats = new ArrayList<>();

        for (Map.Entry<String, Statistics> a : stats.entrySet()) {
            Statistics stats = a.getValue();
            double variance = stats.variance();
            System.err.println("Variance of " + stats.name + ": " + variance);
            if (variance > minVariance) {
                validStats.add(stats);
            }
        }

        // Construct combined header
        String headerFields[] = new String[validStats.size()];
        for (int i = 0; i < headerFields.length; i++) {
            headerFields[i] = validStats.get(i).name;
        }
        if (headerFields.length == 0) {
            System.err.println(getName() + " produced no output");
            return;
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
    }

    private static class Statistics {

        final String name;
        final TDoubleList list = new TDoubleArrayList();
        double sum = 0;
        long realSize = 0;

        Statistics(String name) {
            this.name = name;
        }

        double defaultValue() {
            // This can be changed to use a different filler-value when no real value is present
            // TODO maybe use average?
            return Double.NaN;
        }

        double getValue(int sampleNr) {
            double val = list.get(sampleNr);
            if (Double.isNaN(val)) {
                val = defaultValue();
            }
            return val;
        }

        void fill(int num) {
            // TODO this can be more efficient
            for (int i = 0; i < num; i++) {
                list.add(Double.NaN);
            }
        }

        void add(double val) {
            list.add(val);
            if (!Double.isNaN(val)) {
                sum += val;
                realSize++;
            }
        }

        double average() {
            if (realSize == 0) return 0.0;
            return sum / realSize;
        }

        double variance() {
            long size = realSize;
            double avg = average();
            double stdOffsetSum = 0.0;
            for (int i = 0; i < size; i++) {
                double val = list.get(i);
                if (!Double.isNaN(val)) {
                    double offset = avg - val;
                    double stdOffset = offset*offset;
                    stdOffsetSum += stdOffset;
                }
            }
            return stdOffsetSum / size;
        }

    }

    private static class SampleMetadata {
        Date timestamp;
        String source;
        String label;

        public SampleMetadata(Date timestamp, String source, String label) {
            this.source = source;
            this.label = label;
            this.timestamp = timestamp;
        }
    }

}

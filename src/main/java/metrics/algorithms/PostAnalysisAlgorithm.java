package metrics.algorithms;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import metrics.Sample;
import metrics.io.InputStreamClosedException;
import metrics.io.MetricInputStream;
import metrics.io.MetricOutputStream;

import java.io.IOException;
import java.util.*;

/**
 * This abstract algorithm reads all input samples without outputting any results
 * until the input stream is closed. Only after all input samples are read, the
 * results are written to the output stream.
 * This is not applicable for streaming inputs of indefinite length (like receiving over TCP),
 * and should only be used for finite inputs like CSV files.<br>
 */
public abstract class PostAnalysisAlgorithm<M extends PostAnalysisAlgorithm.MetricLog> extends GenericAlgorithm {

    protected final List<SampleMetadata> samples = new ArrayList<>();
    protected final SortedMap<String, M> metrics = new TreeMap<>();

    protected String currentSource = null;
    protected int numSources = 0;
    private final boolean globalAnalysis;

    public PostAnalysisAlgorithm(boolean globalAnalysis) {
        super();
        this.globalAnalysis = globalAnalysis;
    }

    protected void executeStep(MetricInputStream input, MetricOutputStream output) throws IOException {
        try {
            Sample sample = input.readSample();
            if (sourceChanged(sample.getSource())) {
                numSources++;
                currentSource = sample.getSource();
                if (!globalAnalysis) flushResults(output);
            }
            analyseSample(sample);
        } catch (InputStreamClosedException closedExc) {
            flushResults(output);
            throw closedExc;
        }
    }

    private void flushResults(MetricOutputStream output) {
        if (samples.isEmpty()) return;
        String src = globalAnalysis && numSources > 1 ? String.valueOf(numSources) + " sources" : currentSource;
        System.err.println(toString() + " computing " + samples.size()
                + " samples of " + metrics.size() + " metrics from " + src + "...");
        if (globalAnalysis)
            currentSource = toString();
        try {
            writeResults(output);
        } catch (IOException resultExc) {
            System.err.println("Error writing results of " + toString());
            resultExc.printStackTrace();
        }
        samples.clear();
        metrics.clear();
    }

    private boolean sourceChanged(String newSource) {
        if (currentSource == newSource) {
            return false;
        } else if (currentSource != null && newSource != null) {
            return !currentSource.equals(newSource);
        }
        return true;
    }

    protected abstract void writeResults(MetricOutputStream output) throws IOException;

    private void analyseSample(Sample sample) throws IOException {
        sample.checkConsistency();
        registerMetricData(sample);
    }

    protected void registerMetricData(Sample sample) throws IOException {
        String[] header = sample.getHeader().header;
        Set<String> unhandledStats = new HashSet<>(metrics.keySet());
        double[] values = sample.getMetrics();
        for (int i = 0; i < header.length; i++) {
            MetricLog metric = getStats(header[i]);
            metric.add(values[i]);
            unhandledStats.remove(metric.name); // Might not be contained
        }
        registerSample(sample);
        for (String unhandledHeader : unhandledStats) {
            metrics.get(unhandledHeader).fill(1);
        }
    }

    void registerSample(Sample sample) {
        samples.add(new SampleMetadata(sample.getTimestamp(), sample.getSource(), sample.getLabel()));
    }

    double[] getSampleValues(int sampleNr) {
        double row[] = new double[metrics.size()];
        int metricNr = 0;
        for (MetricLog metricLog : metrics.values()) {
            row[metricNr] = metricLog.getValue(sampleNr);
            metricNr++;
        }
        return row;
    }

    M getStats(String name) {
        M result;
        if ((result = metrics.get(name)) == null) {
            result = createMetricStats(name);
            result.fill(samples.size()); // Fill up missed values
            metrics.put(name, result);
        }
        return result;
    }

    protected abstract M createMetricStats(String name);

    static class SampleMetadata {
        Date timestamp;
        String source;
        String label;

        public SampleMetadata(Date timestamp, String source, String label) {
            this.source = source;
            this.label = label;
            this.timestamp = timestamp;
        }
    }

    static class MetricLog {

        final String name;
        final TDoubleList list = new TDoubleArrayList();
        private double[] vector = null; // Cache for getVector()

        MetricLog(String name) {
            this.name = name;
        }

        double defaultValue() {
            // This is used when accessing individual values to remove occurrences if NaN.
            // Only relevant, if fillValue() returns NaN.
            // TODO maybe use average?
            return Double.NaN;
        }

        double fillValue() {
            // This will be used whenever filling up the log with unknown values
            // and whenever a known NaN-values is to be added.
            return Double.NaN;
        }

        double getValue(int sampleNr) {
            double val = list.get(sampleNr);
            if (Double.isNaN(val)) {
                val = defaultValue();
            }
            return val;
        }

        double[] getVector() {
            if (vector == null) {
                vector = list.toArray();
            }
            return vector;
        }

        void fill(int num) {
            // TODO this can be more efficient
            for (int i = 0; i < num; i++) {
                list.add(fillValue());
            }
            vector = null;
        }

        void add(double val) {
            if (Double.isNaN(val)) {
                val = fillValue();
            }
            list.add(val);
            vector = null;
        }

    }

    static class NoNanMetricLog extends MetricLog {
        NoNanMetricLog(String name) {
            super(name);
        }

        @Override
        double defaultValue() {
            return 0.0;
        }

        @Override
        double fillValue() {
            return 0.0;
        }
    }

    static class MetricStatistics extends MetricLog {

        double sum = 0;
        int realSize = 0;

        MetricStatistics(String name) {
            super(name);
        }

        @Override
        void add(double val) {
            super.add(val);
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
            double avg = average();
            long size = realSize;
            double stdOffsetSum = 0.0;
            for (int i = 0; i < size; i++) {
                double val = list.get(i);
                if (!Double.isNaN(val)) {
                    double offset = avg - val;
                    double stdOffset = offset * offset;
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

    static class ExtendedMetricsStats extends MetricStatistics {

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        ExtendedMetricsStats(String name) {
            super(name);
        }

        @Override
        void add(double val) {
            super.add(val);
            if (!Double.isNaN(val)) {
                min = Double.min(min, val);
                max = Double.min(max, val);
            }
        }

    }

}

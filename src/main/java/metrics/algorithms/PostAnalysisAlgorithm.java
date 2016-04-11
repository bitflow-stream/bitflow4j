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
 * and should only be used for finite inputs like CSV files.
 */
public abstract class PostAnalysisAlgorithm<M extends PostAnalysisAlgorithm.Metric> extends GenericAlgorithm {

    protected final List<SampleMetadata> samples = new ArrayList<>();
    protected final SortedMap<String, M> metrics = new TreeMap<>();

    public PostAnalysisAlgorithm() {
        super();
    }

    public PostAnalysisAlgorithm(String name) {
        super(name);
    }

    protected void executeStep(MetricInputStream input, MetricOutputStream output) throws IOException {
        try {
            Sample sample = input.readSample();
            analyseSample(sample);
        } catch(InputStreamClosedException closedExc) {
            System.err.println("Starting analysis phase of " + getName());
            try {
                writeResults(output);
            } catch (Exception resultExc) {
                System.err.println("Error writing results of " + getName());
                resultExc.printStackTrace();
            }
            throw closedExc;
        }
    }

    protected abstract void analyseSample(Sample sample) throws IOException;

    protected abstract void writeResults(MetricOutputStream output) throws IOException;

    protected void registerSample(Sample sample) {
        samples.add(new SampleMetadata(sample.getTimestamp(), sample.getSource(), sample.getLabel()));
    }

    protected M getStats(String name) {
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

    static class Metric {

        final String name;
        final TDoubleList list = new TDoubleArrayList();
        long realSize = 0;

        Metric(String name) {
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
                realSize++;
            }
        }

    }

}

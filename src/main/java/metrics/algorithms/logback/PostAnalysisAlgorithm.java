package metrics.algorithms.logback;

import metrics.Sample;
import metrics.io.InputStreamClosedException;
import metrics.io.MetricInputStream;
import metrics.io.MetricOutputStream;

import java.io.IOException;

/**
 * This abstract algorithm reads all input samples without outputting any results
 * until the input stream is closed. Only after all input samples are read, the
 * results are written to the output stream.
 * This is not applicable for streaming inputs of indefinite length (like receiving over TCP),
 * and should only be used for finite inputs like CSV files.
 * <br>
 * As an option, setting {@link #globalAnalysis} to false will cause the output to be flushed
 * whenever an incoming {@link Sample} has a new {@link Sample#getSource() source} field.
 */
public abstract class PostAnalysisAlgorithm<M extends MetricLog> extends LogbackAlgorithm<M> {

    public  String currentSource = null;
    public int numSources = 0;
    private final boolean globalAnalysis;

    public PostAnalysisAlgorithm(boolean globalAnalysis) {
        super();
        this.globalAnalysis = globalAnalysis;
    }

    protected abstract void writeResults(MetricOutputStream output) throws IOException;

    @Override
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

    private void analyseSample(Sample sample) throws IOException {
        sample.checkConsistency();
        registerMetricData(sample);
    }

}

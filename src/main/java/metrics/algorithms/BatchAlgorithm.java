package metrics.algorithms;

import metrics.Sample;
import metrics.io.InputStreamClosedException;
import metrics.io.MetricInputStream;
import metrics.io.MetricOutputStream;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by anton on 5/8/16.
 */
public abstract class BatchAlgorithm extends AbstractAlgorithm {

    boolean flushSampleSources = false;
    Set<String> sources = new HashSet<>();
    public String currentSource = null;

    /**
     * Compute and output results, then clear any logged data so that a new batch of data
     * can be computed. Clear any intermediate data so that a new batch can be started.
     */
    protected abstract void flushAndClearResults(MetricOutputStream output) throws IOException;

    protected abstract void addSample(Sample sample);

    @Override
    protected void executeStep(MetricInputStream input, MetricOutputStream output) throws IOException {
        try {
            Sample sample = input.readSample();
            printStarted();
            if (sourceChanged(sample.getSource())) {
                if (flushSampleSources) {
                    flushAndClearResults(output);
                    sources.clear();
                }
                sources.add(sample.getSource());
                currentSource = sample.getSource();
            }
            sample.checkConsistency();
            handleSample(sample, output);
        } catch (InputStreamClosedException closedExc) {
            flushAndClearResults(output);
            throw closedExc;
        }
    }

    // Hook for subclasses
    protected void handleSample(Sample sample, MetricOutputStream output) throws IOException {
        addSample(sample);
    }

    @Override
    protected final Sample executeSample(Sample sample) throws IOException {
        throw new UnsupportedOperationException("WindowBatchAlgorithm.executeSample() should not be used");
    }

    public BatchAlgorithm flushSampleSources() {
        this.flushSampleSources = true;
        return this;
    }

    @SuppressWarnings("StringEquality")
    private boolean sourceChanged(String newSource) {
        if (currentSource == newSource) {
            return false;
        } else if (currentSource != null && newSource != null) {
            return !currentSource.equals(newSource);
        }
        return true;
    }

}

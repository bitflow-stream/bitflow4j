package metrics.algorithms;

import metrics.Sample;
import metrics.io.MetricOutputStream;
import metrics.main.misc.ParameterHash;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by anton on 5/14/16.
 * <p>
 * Optionally use a number of samples for "training" first (collect in window), then start outputting
 * additional samples directly. If {@link #trainingInstances} is <= 0, ignore this and collect all samples
 * in the window until it is flushed (same as WindowBatchAlgorithm).
 */
public abstract class TrainableWindowAlgorithm extends WindowBatchAlgorithm {

    private static final Logger logger = Logger.getLogger(TrainableWindowAlgorithm.class.getName());

    private final int trainingInstances;
    private boolean trainingDone = false;

    public TrainableWindowAlgorithm(int trainingInstances) {
        this.trainingInstances = trainingInstances;
    }

    public TrainableWindowAlgorithm() {
        this(0);
    }

    // Output values for every Sample in the window, without clearing the window.
    protected abstract void flushResults(MetricOutputStream output) throws IOException;

    // Output a single instance based on the data previously trained.
    protected abstract void outputSingleInstance(Sample sample, MetricOutputStream output) throws IOException;

    @Override
    protected void flushAndClearResults(MetricOutputStream output) throws IOException {
        if (trainingInstances <= 0) {
            super.flushAndClearResults(output);
        } else {
            // All samples already handled, just clear the window
            getWindow().clear();
            trainingDone = false;
        }
    }

    @Override
    protected void handleSample(Sample sample, MetricOutputStream output) throws IOException {
        if (trainingInstances <= 0 || getWindow().numSamples() < trainingInstances) {
            addSample(sample);
        } else {
            if (getWindow().numSamples() == trainingInstances && !trainingDone) {
                // Training is finished -> first handle training samples
                logger.info(toString() + " finished training " + trainingInstances + " samples");
                flushResults(output);
                trainingDone = true;
            }
            outputSingleInstance(sample, output);
        }
    }

    @Override
    public void hashParameters(ParameterHash hash) {
        super.hashParameters(hash);
        hash.writeInt(trainingInstances);
    }

}

package bitflow4j.steps.batch;

import bitflow4j.Sample;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by anton on 5/14/16.
 * <p>
 * Optionally use a number of samples for "training" first (collect in window), then start outputting
 * additional samples directly. If {@link #trainingInstances} is <= 0, ignore this and collect all samples
 * in the window until it is flushed (same as WindowBatchPipelineStep).
 */
public abstract class TrainableWindow extends WindowBatchPipelineStep {

    private static final Logger logger = Logger.getLogger(TrainableWindow.class.getName());

    private final int trainingInstances;
    private boolean trainingDone = false;

    public TrainableWindow(int trainingInstances) {
        this.trainingInstances = trainingInstances;
    }

    public TrainableWindow() {
        this(0);
    }

    // Output a single instance based on the data previously trained.
    protected abstract void outputSingleInstance(Sample sample) throws IOException;

    @Override
    protected void flushAndClearResults() throws IOException {
        if (trainingInstances <= 0) {
            super.flushAndClearResults();
        } else {
            // All samples already handled, just clear the window
            getWindow().clear();
            trainingDone = false;
        }
    }

    @Override
    protected void handleSample(Sample sample) throws IOException {
        if (trainingInstances <= 0 || getWindow().numSamples() < trainingInstances) {
            addSample(sample);
        } else {
            if (getWindow().numSamples() == trainingInstances && !trainingDone) {
                // Training is finished -> first push training samples
                logger.info(toString() + " finished training " + trainingInstances + " samples");
                flushResults();
                trainingDone = true;
            }
            outputSingleInstance(sample);
        }
    }

}
package bitflow4j.steps.batch;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;
import bitflow4j.io.marshall.InputStreamClosedException;

import java.io.IOException;

/**
 * Instead of immediately handling every Sample, fill up a obsolete of samples and then push all of them at once, possibly outputting a
 * different number of resulting Samples.
 * <p>
 * Created by anton on 5/8/16.
 */
public abstract class BatchPipelineStep extends AbstractPipelineStep {

    /**
     * Compute and output results, then clear any logged data so that a new obsolete of data can be computed. Clear any intermediate data so
     * that a new obsolete can be started.
     */
    protected abstract void flushAndClearResults() throws IOException;

    protected abstract void addSample(Sample sample);

    // Hook for subclasses
    protected void handleSample(Sample sample) throws IOException {
        addSample(sample);
    }

    @Override
    public final void writeSample(Sample sample) throws IOException {
        try {
//            flushAndClearResults();
            sample.checkConsistency();
            handleSample(sample);
        } catch (InputStreamClosedException closedExc) {
            flushAndClearResults();
            throw closedExc;
        }
    }

    @Override
    protected void doClose() throws IOException {
        flushAndClearResults();
        super.doClose();
    }

}

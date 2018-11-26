package bitflow4j.steps.batch;

import bitflow4j.Sample;
import bitflow4j.window.AbstractSampleWindow;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Samples are collected in batches in then flushed. Flushing happens when the input stream closes,
 * optionally when the source field of incoming samples changes, or manually on any other condition.
 */
public abstract class WindowBatchPipelineStep extends BatchPipelineStep {

    private static final Logger logger = Logger.getLogger(WindowBatchPipelineStep.class.getName());

    /**
     * Compute and output results, then clear any logged data so that a new obsolete of data
     * can be computed.
     */
    protected abstract void flushResults() throws IOException;

    protected abstract AbstractSampleWindow getWindow();

    @Override
    protected void flushAndClearResults() throws IOException {
        printFlushMessage(getWindow().numSamples(), getWindow().numMetrics());
        flushResults();
        getWindow().clear();
    }

    @Override
    protected void addSample(Sample sample) {
        getWindow().add(sample);
    }

    private void printFlushMessage(int numSamples, int numMetrics) {
        String info = "";
        boolean hasInfo = false;
        if (numSamples > 0) {
            info += "(" + numSamples + " samples";
            hasInfo = true;
        }
        if (numMetrics > 0) {
            if (hasInfo)
                info += ", ";
            else
                info += "(";
            info += numMetrics + " bitflow4j";
            hasInfo = true;
        }
        if (hasInfo) info += ") ";
        String message = toString() + " computing results " + info;
        logger.info(message);
    }

}

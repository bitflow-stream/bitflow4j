package metrics.algorithms;

import metrics.Sample;
import metrics.io.MetricOutputStream;
import metrics.io.window.AbstractSampleWindow;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Samples are collected in batches in then flushed. Flushing happens when the input stream closes,
 * optionally when the source field of incoming samples changes, or manually on any other condition.
 */
public abstract class WindowBatchAlgorithm extends BatchAlgorithm {

    private static final Logger logger = Logger.getLogger(WindowBatchAlgorithm.class.getName());

    /**
     * Compute and output results, then clear any logged data so that a new obsolete of data
     * can be computed.
     */
    protected abstract void flushResults(MetricOutputStream output) throws IOException;

    protected abstract AbstractSampleWindow getWindow();

    @Override
    protected void flushAndClearResults(MetricOutputStream output) throws IOException {
        printFlushMessage(getWindow().numSamples(), getWindow().numMetrics());
        flushResults(output);
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
            info += numMetrics + " metrics";
            hasInfo = true;
        }
        if (hasInfo) info += ") ";
        String sourceStr = (flushSampleSources || sources.size() <= 1) ? currentSource : sources.size() + " sources";
        String message = toString() + " computing results " + info + "from " + sourceStr + "...";
        logger.info(message);
    }

}

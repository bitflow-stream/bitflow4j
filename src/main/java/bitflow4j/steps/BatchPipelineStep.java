package bitflow4j.steps;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Instead of immediately handling every Sample, fill up a obsolete of samples and then push all of them at once, possibly outputting a
 * different number of resulting Samples.
 * <p>
 * Created by anton on 5/8/16.
 */
public abstract class BatchPipelineStep extends AbstractPipelineStep {

    private static final Logger logger = Logger.getLogger(BatchPipelineStep.class.getName());

    // TODO implement automatic flushing:
    // window size, timeout (wall clock, sample timestamps), tag change

    private final String batchSeparationTag;
    private boolean warnedMissingSeparationTag = false;
    private String previousSeparationTagValue = null;

    public BatchPipelineStep() {
        batchSeparationTag = null;
    }

    private List<Sample> window = new ArrayList<>();

    protected abstract void flush(List<Sample> window) throws IOException;

    @Override
    public final void writeSample(Sample sample) throws IOException {
        window.add(sample);
        if (shouldFlush(sample))
            flushResults();
    }

    private boolean shouldFlush(Sample sample) {
        if (batchSeparationTag != null) {
            if (!sample.hasTag(batchSeparationTag)) {
                if (!warnedMissingSeparationTag) {
                    logger.warning("BatchPipelineStep: Dropping samples without '" + batchSeparationTag + "' tag");
                    warnedMissingSeparationTag = true;
                }
            } else {
                String tagValue = sample.getTag(batchSeparationTag);
                boolean tagChanged = previousSeparationTagValue != null && !tagValue.equals(previousSeparationTagValue);
                previousSeparationTagValue = tagValue;
                if (tagChanged)
                    return true;
            }
        }

        return false;
    }

    @Override
    protected void doClose() throws IOException {
        flushResults();
        super.doClose();
    }

    private void flushResults() throws IOException {
        printFlushMessage();
        flush(window);
        window.clear();
    }

    private void printFlushMessage() {
        String info = "";
        if (window.isEmpty()) {
            info = " (no samples)";
        } else {
            int numSamples = window.size();
            int numMetrics = window.get(0).getMetrics().length;
            info += "(" + numSamples + " samples, " + numMetrics + " metrics)";
        }
        logger.info(toString() + " computing results " + info);
    }

}

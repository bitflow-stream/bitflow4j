package bitflow4j.steps;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;
import bitflow4j.task.LoopTask;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Instead of immediately handling every Sample, fill up a obsolete of samples and then push all of them at once, possibly outputting a
 * different number of resulting Samples.
 * <p>
 * Created by anton on 5/8/16.
 */
public abstract class BatchPipelineStep extends AbstractPipelineStep {

    protected static final Logger logger = Logger.getLogger(BatchPipelineStep.class.getName());

    private final String batchSeparationTag;
    private boolean warnedMissingSeparationTag = false;
    private String previousSeparationTagValue = null;

    private final long timeoutMs;
    private long startTime;

    public BatchPipelineStep() {
        this(null);
    }

    public BatchPipelineStep(String batchSeparationTag) {
        this(batchSeparationTag, 0);
    }

    public BatchPipelineStep(String batchSeparationTag, long timeoutMs) {
        this.batchSeparationTag = batchSeparationTag;
        this.timeoutMs = timeoutMs;
    }

    private List<Sample> window = new ArrayList<>();

    protected abstract void flush(List<Sample> window) throws IOException;

    @Override
    public void start(TaskPool pool) throws IOException {
        super.start(pool);

        // TODO refactor for allowing additional flushing modes:
        // window size, timeout (wall clock, sample timestamps), tag change. Micro batching ("jumping" window) vs moving window.

        if (timeoutMs > 0) {
            startTime = new Date().getTime();
            pool.start(new LoopTask() {
                @Override
                public String toString() {
                    return "Auto-Flush Task for " + BatchPipelineStep.this.toString();
                }

                @Override
                protected boolean executeIteration() throws IOException {
                    long currentTime = new Date().getTime();
                    if (currentTime - startTime > timeoutMs) {
                        try {
                            boolean flushed = flushResults();
                            if(flushed){
                                logger.log(Level.INFO, "Flushed batch due to timeout (" + timeoutMs + "ms).");
                            }
                        } catch (IOException ex) {
                            logger.log(Level.SEVERE, "Failed to automatically flush batch", ex);
                        }
                    }
                    return pool.sleep(timeoutMs / 2);
                }
            });
        }
    }

    @Override
    public final synchronized void writeSample(Sample sample) throws IOException {
        startTime = new Date().getTime();
        if (shouldFlush(sample)) {
            flushResults();
        }
        window.add(sample);
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
                return tagChanged;
            }
        }
        return false;
    }

    @Override
    protected synchronized void doClose() throws IOException {
        flushResults();
        super.doClose();
    }

    private synchronized boolean flushResults() throws IOException {
        if (window.isEmpty())
            return false;
        printFlushMessage();
        flush(window);
        window.clear();
        return true;
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

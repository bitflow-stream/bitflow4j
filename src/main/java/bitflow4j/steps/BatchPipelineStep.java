package bitflow4j.steps;

import bitflow4j.Sample;
import bitflow4j.script.registry.BitflowConstructor;
import bitflow4j.script.registry.Optional;
import bitflow4j.task.LoopTask;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;

/**
 * Instead of immediately handling every Sample, fill up a list of samples based on a number of criteria (time, tag values, ...).
 * The resulting list is then processed through a number of BatchHandlers, which possibly output a different number of samples in the end.
 * Afterwards, the resulting list of samples is forwarded to the subsequent processing step in the resulting order.
 * <p>
 * The class is marked as final, because the obsolete way to use it was subclassing. Now, one or more BatchHandler instances must be added
 * through the addBatchHandler() method instead.
 * <p>
 * Created by anton on 5/8/16.
 */
public final class BatchPipelineStep extends AbstractBatchPipelineStep {
    private boolean warnedMissingSeparationTag = false;
    private String previousSeparationTagValue = null;
    final String batchSeparationTag;
    final long timeoutMs;
    long startTime;

    @BitflowConstructor
    public BatchPipelineStep(@Optional String batchSeparationTag, @Optional long timeoutMs, BatchHandler... handlers) {
        super(handlers);
        this.batchSeparationTag = batchSeparationTag;
        this.timeoutMs = timeoutMs;
    }

    public BatchPipelineStep(String batchSeparationTag, BatchHandler... handlers) {
        this(batchSeparationTag, 0, handlers);
    }

    public BatchPipelineStep(BatchHandler... handlers) {
        this((String) null, handlers);
    }

    @Override
    public void threadIteration(TaskPool pool) throws IOException {
        if (timeoutMs > 0) {
            startTime = new Date().getTime();
            pool.start(new LoopTask() {
                @Override
                public String toString() {
                    return "Auto-Flush Task for " + BatchPipelineStep.this.toString();
                }

                @Override
                protected boolean executeIteration() throws IOException {
                    long currentTime = System.currentTimeMillis();
                    checkForFlush(currentTime);
                    return pool.sleep(timeoutMs / 2);
                }
            });
        }
    }

    @Override
    public void checkForFlush(long currentTime) throws IOException {
        if (currentTime - startTime > timeoutMs) {
            try {
                boolean flushed = flushResults();
                if (flushed) {
                    logger.log(Level.INFO, String.format("Flushed batch due to timeout (%sms) for step: %s.", timeoutMs, BatchPipelineStep.this.toString()));
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Failed to automatically flush batch", ex);
            }
        }
    }

    @Override
    public void addSample(Sample sample) throws IOException {
        startTime = new Date().getTime();
        if (shouldFlush(sample)) {
            flushResults();
        }
        window.add(sample);

    }

    @Override
    public void closeCleanup() throws IOException {
        flushResults();
    }

    private boolean shouldFlush(Sample sample) {
        if (batchSeparationTag != null && !batchSeparationTag.isEmpty()) {
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
}

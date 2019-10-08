package bitflow4j.steps;

import bitflow4j.Sample;
import bitflow4j.script.registry.BitflowConstructor;
import bitflow4j.script.registry.Optional;
import bitflow4j.script.registry.RegisteredParameter;
import bitflow4j.script.registry.RegisteredParameterList;
import bitflow4j.task.LoopTask;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    private final String batchSeparationTag;
    private final long timeoutMs;

    private long startTime;
    private boolean warnedMissingSeparationTag = false;
    private String previousSeparationTagValue = null;

    private final List<Sample> window = new ArrayList<>();

    @BitflowConstructor
    public BatchPipelineStep(@Optional String batchSeparationTag, @Optional long timeoutMs) {
        this(batchSeparationTag, timeoutMs, new BatchHandler[0]);
    }

    public BatchPipelineStep(String batchSeparationTag, long timeoutMs, BatchHandler... handlers) {
        super(handlers);
        this.batchSeparationTag = batchSeparationTag;
        this.timeoutMs = timeoutMs;
    }

    public BatchPipelineStep(String batchSeparationTag, BatchHandler... handlers) {
        this(batchSeparationTag, 0, handlers);
    }

    public BatchPipelineStep(BatchHandler... handlers) {
        this(null, handlers);
    }

    public static final RegisteredParameterList PARAMETER_LIST = new RegisteredParameterList(
            new RegisteredParameter[]{
                    new RegisteredParameter("separationTag", RegisteredParameter.ContainerType.Primitive, String.class, ""),
                    new RegisteredParameter("timeout", RegisteredParameter.ContainerType.Primitive, Long.class, 0L),
                    mergeModeParameter
            });

    public static BatchPipelineStep createFromParameters(Map<String, Object> params) {
        String separationTag = null;
        if (params.containsKey("separationTag")) {
            separationTag = (String) params.get("separationTag");
        }
        long timeout = 0;
        if (params.containsKey("timeout")) {
            timeout = (Long) params.get("timeout");
        }
        return new BatchPipelineStep(separationTag, timeout);
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
                boolean flushed = flush();
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
            flush();
        }
        window.add(sample);
    }

    @Override
    public void closeCleanup() throws IOException {
        flush();
    }

    private boolean flush() throws IOException {
        if (window.isEmpty())
            return false;
        flushWindow(window);
        window.clear();
        return true;
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

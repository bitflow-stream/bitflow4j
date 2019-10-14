package bitflow4j.steps;

import bitflow4j.Sample;
import bitflow4j.script.registry.BitflowConstructor;
import bitflow4j.script.registry.Optional;
import bitflow4j.script.registry.RegisteredParameter;
import bitflow4j.script.registry.RegisteredParameterList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final Logger logger = Logger.getLogger(BatchPipelineStep.class.getName());

    private final String batchSeparationTag;
    private final long timeoutMs;

    private long lastSampleReceived = -1;
    private boolean warnedMissingSeparationTag = false;
    private String previousSeparationTagValue = null;

    private final List<Sample> window = new ArrayList<>();

    @BitflowConstructor
    public BatchPipelineStep(@Optional String batchSeparationTag, @Optional long timeoutMs) {
        this(batchSeparationTag, timeoutMs, new BatchHandler[0]);
    }

    public BatchPipelineStep(String batchSeparationTag, long timeoutMs, BatchHandler... handlers) {
        super(timeoutMs / 2, handlers);
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
    public void checkConcurrentFlush() {
        if (lastSampleReceived > 0 && System.currentTimeMillis() - lastSampleReceived > timeoutMs) {
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
        // TODO is new Date().getTime() better than currentTimeMillis()? Also check other usages of currentTimeMillis().
        lastSampleReceived = System.currentTimeMillis();
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

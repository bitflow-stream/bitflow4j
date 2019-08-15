package bitflow4j.steps;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;
import bitflow4j.misc.TreeFormatter;
import bitflow4j.script.registry.BitflowConstructor;
import bitflow4j.script.registry.Optional;
import bitflow4j.script.registry.RegisteredParameter;
import bitflow4j.script.registry.RegisteredParameterList;
import bitflow4j.task.LoopTask;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.*;
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
public final class BatchPipelineStep extends AbstractPipelineStep implements TreeFormatter.FormattedNode {

    protected static final Logger logger = Logger.getLogger(BatchPipelineStep.class.getName());

    private final String batchSeparationTag;
    private boolean warnedMissingSeparationTag = false;
    private String previousSeparationTagValue = null;

    private final long timeoutMs;
    private long startTime;

    private List<Sample> window = new ArrayList<>();
    private final List<BatchHandler> handlers = new ArrayList<>();

    public BatchPipelineStep() {
        this((String) null);
    }

    public BatchPipelineStep(BatchHandler... handlers) {
        this(null, handlers);
    }

    public BatchPipelineStep(String batchSeparationTag) {
        this(batchSeparationTag, 0);
    }

    public BatchPipelineStep(String batchSeparationTag, BatchHandler... handlers) {
        this(batchSeparationTag, 0, handlers);
    }

    @BitflowConstructor
    public BatchPipelineStep(@Optional String batchSeparationTag, @Optional long timeoutMs) {
        this.batchSeparationTag = batchSeparationTag;
        this.timeoutMs = timeoutMs;
    }

    public BatchPipelineStep(String batchSeparationTag, long timeoutMs, BatchHandler... handlers) {
        this.batchSeparationTag = batchSeparationTag;
        this.timeoutMs = timeoutMs;
        this.handlers.addAll(Arrays.asList(handlers));
    }

    public static final RegisteredParameterList BATCH_STEP_PARAMETERS = new RegisteredParameterList(
            new RegisteredParameter[]{
                    new RegisteredParameter("separationTag", RegisteredParameter.ContainerType.Primitive, String.class, ""),
                    new RegisteredParameter("timeout", RegisteredParameter.ContainerType.Primitive, Long.class, 0L)
            });

    public static BatchPipelineStep createFromParameters(Map<String, Object> params) throws IllegalArgumentException {
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

    public void addBatchHandler(BatchHandler handler) {
        handlers.add(handler);
    }

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
                            if (flushed) {
                                logger.log(Level.INFO, String.format("Flushed batch due to timeout (" + timeoutMs + "ms) for step: %s.", BatchPipelineStep.this.toString()));
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

    private void flush(List<Sample> window) throws IOException {
        for (BatchHandler handler : handlers) {
            window = handler.handleBatch(window);
        }
        for (Sample sample : window) {
            this.output().writeSample(sample);
        }
    }

    // =================================================
    // Printing ========================================
    // =================================================
    public String toString() {
        String firstHandler = "";
        if(handlers.size() >= 1){
            firstHandler = handlers.get(0).getClass().getSimpleName();
        }
        return String.format("Batch processing, %s handler(s): %s", handlers.size(), firstHandler);
    }

    @Override
    public Collection<Object> formattedChildren() {
        List<Object> children = new ArrayList<>(handlers.size());
        children.addAll(handlers);
        return children;
    }

}

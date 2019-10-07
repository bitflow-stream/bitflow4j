package bitflow4j.steps;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;
import bitflow4j.misc.TreeFormatter;
import bitflow4j.script.registry.RegisteredParameter;
import bitflow4j.script.registry.RegisteredParameterList;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.*;
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
public abstract class AbstractBatchPipelineStep extends AbstractPipelineStep implements TreeFormatter.FormattedNode {

    protected static final Logger logger = Logger.getLogger(AbstractBatchPipelineStep.class.getName());

    protected List<Sample> window = new ArrayList<>();
    private final List<BatchHandler> handlers = new ArrayList<>();

    public AbstractBatchPipelineStep(BatchHandler... handlers) {
        this.handlers.addAll(Arrays.asList(handlers));
    }

    public static final RegisteredParameterList BATCH_STEP_PARAMETERS = new RegisteredParameterList(
            new RegisteredParameter[]{
                    new RegisteredParameter("separationTag", RegisteredParameter.ContainerType.Primitive, String.class, ""),
                    new RegisteredParameter("timeout", RegisteredParameter.ContainerType.Primitive, Long.class, 0L),
                    new RegisteredParameter("mergeMode", RegisteredParameter.ContainerType.Primitive, Boolean.class, false)
            });

    public static AbstractBatchPipelineStep createFromParameters(Map<String, Object> params) throws IllegalArgumentException {
        if (params.containsKey("mergeMode") && (Boolean) params.get("mergeMode")) {
            return createMergeBatchPipelineStep(params);
        } else {
            return createBatchPipelineStep(params);
        }
    }

    private static MergeBatchPipelineStep createMergeBatchPipelineStep(Map<String, Object> params) {
        String separationTag = null;
        if (params.containsKey("separationTag")) {
            separationTag = (String) params.get("separationTag");
        }
        long timeout = 0;
        if (params.containsKey("timeout")) {
            timeout = (Long) params.get("timeout");
        }
        return new MergeBatchPipelineStep(separationTag, timeout);
    }

    private static BatchPipelineStep createBatchPipelineStep(Map<String, Object> params) {
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
        threadIteration(pool);
    }

    public abstract void threadIteration(TaskPool pool) throws IOException;

    public abstract void checkForFlush(long currentTime) throws IOException;

    public abstract void addSample(Sample sample) throws IOException;

    public abstract void closeCleanup() throws IOException;

    @Override
    public final synchronized void writeSample(Sample sample) throws IOException {
        addSample(sample);
    }

    @Override
    protected synchronized void doClose() throws IOException {
        closeCleanup();
        super.doClose();
    }

    protected synchronized boolean flushResults() throws IOException {
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
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < handlers.size(); i++) {
            sb.append(handlers.get(i).getClass().getSimpleName());
            if (i < handlers.size() - 1) {
                sb.append(", ");
            }
        }
        return String.format("Batch processing, %s handler(s): %s", handlers.size(), sb.toString());
    }

    @Override
    public Collection<Object> formattedChildren() {
        List<Object> children = new ArrayList<>(handlers.size());
        children.addAll(handlers);
        return children;
    }

}

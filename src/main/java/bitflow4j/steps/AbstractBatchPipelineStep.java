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
 * This abstract class allows implementing batch processing steps. These steps do not immediately process incoming smaples,
 * but instead collect them until a given criterion is met, and then use a list of BatchHandlers to process a list of collected
 * samples, before forwarding the results.
 * <p>
 * By subclassing this class, new modes of batching can be introduced, that can be reused in different situations.
 * One or more BatchHandler instances must be added through the addBatchHandler() method of a concrete subclass.
 * <p>
 * Created by anton on 5/8/16.
 */
public abstract class AbstractBatchPipelineStep extends AbstractPipelineStep implements TreeFormatter.FormattedNode {

    protected static final Logger logger = Logger.getLogger(AbstractBatchPipelineStep.class.getName());

    private final List<BatchHandler> handlers = new ArrayList<>();

    public AbstractBatchPipelineStep(BatchHandler... handlers) {
        this.handlers.addAll(Arrays.asList(handlers));
    }

    // This parameter determines which batch mode is used. When more modes are introduced, it should be changed to a string parameter.
    public static final RegisteredParameter mergeModeParameter = new RegisteredParameter("mergeMode", RegisteredParameter.ContainerType.Primitive, Boolean.class, false);

    public static RegisteredParameterList getParameterList(Map<String, Object> rawParameters) {
        Object val = rawParameters.get("mergeMode");
        if (mergeModeParameter.canParse(val) && (Boolean) mergeModeParameter.parseValue(val)) {
            return MergeBatchPipelineStep.PARAMETER_LIST;
        } else {
            return BatchPipelineStep.PARAMETER_LIST;
        }
    }

    public static AbstractBatchPipelineStep createFromParameters(Map<String, Object> params) throws IllegalArgumentException {
        if (params.containsKey("mergeMode") && (Boolean) params.get("mergeMode")) {
            return MergeBatchPipelineStep.createFromParameters(params);
        } else {
            return BatchPipelineStep.createFromParameters(params);
        }
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

    protected synchronized void flushWindow(List<Sample> window) throws IOException {
        if (window.isEmpty())
            return;
        printFlushMessage(window);
        flush(window);
    }

    private void printFlushMessage(List<Sample> window) {
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

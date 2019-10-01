package bitflow4j;

import bitflow4j.io.file.FileSink;
import bitflow4j.io.file.FileSource;
import bitflow4j.io.marshall.BinaryMarshaller;
import bitflow4j.io.marshall.CsvMarshaller;
import bitflow4j.misc.TreeFormatter;
import bitflow4j.steps.BatchHandler;
import bitflow4j.steps.BatchPipelineStep;
import bitflow4j.task.ParallelTask;
import bitflow4j.task.StoppableTask;
import bitflow4j.task.Task;
import bitflow4j.task.TaskPool;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Pipeline implements TreeFormatter.FormattedNode {

    protected static final Logger logger = Logger.getLogger(Pipeline.class.getName());

    public Source source;
    public final List<PipelineStep> steps = new ArrayList<>();

    // =================================================
    // Printing ========================================
    // =================================================
    public String toString() {
        return "Pipeline";
    }

    @Override
    public Collection<Object> formattedChildren() {
        List<Object> children = new ArrayList<>();
        if (source != null) {
            children.add(source);
        }
        children.addAll(steps);
        return children;
    }

    // ===============================================
    // Inputs ========================================
    // ===============================================
    public Pipeline input(Source input) {
        if (this.source != null) {
            throw new IllegalStateException("sink was already configured");
        }
        this.source = input;
        return this;
    }

    // TODO remove
    public Pipeline inputBinary(String... files) throws IOException {
        return input(new FileSource(new BinaryMarshaller(), files));
    }

    // TODO remove
    public Pipeline inputCsv(String... files) throws IOException {
        return input(new FileSource(new CsvMarshaller(), files));
    }

    // ===============================================
    // Steps & output ================================
    // ===============================================
    public Pipeline step(PipelineStep algo) {
        steps.add(algo);
        return this;
    }

    public Pipeline batch(String separationTag, long timeout, boolean mapMode, BatchHandler... steps) {
        return step(new BatchPipelineStep(separationTag, timeout, mapMode, steps));
    }

    public Pipeline batch(String separationTag, long timeout, BatchHandler... steps) {
        return step(new BatchPipelineStep(separationTag, timeout, steps));
    }

    public Pipeline batch(String separationTag, BatchHandler... steps) {
        return step(new BatchPipelineStep(separationTag, steps));
    }

    public Pipeline batch(BatchHandler... steps) {
        return step(new BatchPipelineStep(steps));
    }

    // TODO remove
    public Pipeline outputCsv(String filename) throws IOException {
        return step(new FileSink(filename, new CsvMarshaller()));
    }

    // =========================================
    // Running =================================
    // =========================================
    public void runAndWait() {
        runAndWait(new TaskPool());
    }

    public void runAndWait(TaskPool pool) {
        try {
            PipelineStep lastStep = run(pool);
            lastStep.waitUntilClosed();
            pool.stop("PipelineSteps finished");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error starting pipeline", e);
            pool.stop("Error starting pipeline");
        }
        pool.waitForTasks();
    }

    public PipelineStep run(TaskPool pool) throws IOException {
        Source firstSource = source;
        if (firstSource == null) {
            firstSource = new EmptySource();
        }

        List<Task> tasks = new ArrayList<>(steps.size() + 2);
        Source source = firstSource;
        for (PipelineStep step : steps) {
            step = new ConsistencyCheckWrapper(step);
            source.setOutgoingSink(step);
            source = step;
            tasks.add(step);
        }

        // Make sure every pipeline step has a valid outgoing sink
        PipelineStep lastStep = new DropStep();
        source.setOutgoingSink(lastStep);
        tasks.add(lastStep);

        // Initialize and start all pipeline steps
        // Start in reverse order to make sure the sinks are initialized before the sources start pushing data into them
        for (Task task : Lists.reverse(tasks)) {
            pool.start(task);
        }

        // Make the source stoppable. This will trigger a clean shutdown process once the TaskPool is stopped.
        pool.start(wrapSource(firstSource));
        return lastStep;
    }

    public static StoppableTask wrapSource(Source source) {
        if (source instanceof ParallelTask) {
            return new StoppableParallelSourceWrapper(source);
        } else {
            return new StoppableSourceWrapper(source);
        }
    }

}

package bitflow4j;

import bitflow4j.task.StoppableTask;
import bitflow4j.task.TaskPool;

import java.io.IOException;

/**
 * This wrapper makes a Source instance stoppable by wrapping it in a StoppableTask interface.
 * The stop method is implemented by calling the close method of the wrapped Source object.
 * The reason for the strict distinction between the stop and the close method is that the close methods
 * are supposed to be called in sequence, starting from the first source, and continuing down the chain
 * of pipeline steps. The stop method on the other hand has no defined order of execution, therefore a PipelineStep
 * should not implement StoppableTask.
 */
public class StoppableSourceWrapper implements Source, StoppableTask {

    private final Source source;

    public StoppableSourceWrapper(Source source) {
        this.source = source;
    }

    @Override
    public void stop() {
        this.source.close();
    }

    @Override
    public void start(TaskPool pool) throws IOException {
        this.source.start(pool);
    }

    @Override
    public void setOutgoingSink(PipelineStep sink) {
        this.source.setOutgoingSink(sink);
    }

    @Override
    public void close() {
        this.source.close();
    }

}

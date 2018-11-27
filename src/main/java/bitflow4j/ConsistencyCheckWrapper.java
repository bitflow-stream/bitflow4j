package bitflow4j;

import bitflow4j.task.TaskPool;

import java.io.IOException;

public class ConsistencyCheckWrapper implements PipelineStep {

    private final PipelineStep wrapped;

    public ConsistencyCheckWrapper(PipelineStep wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        sample.checkConsistency();
        wrapped.writeSample(sample);
    }

    @Override
    public void setOutgoingSink(PipelineStep sink) {
        wrapped.setOutgoingSink(sink);
    }

    @Override
    public void close() {
        wrapped.close();
    }

    @Override
    public void waitUntilClosed() {
        wrapped.waitUntilClosed();
    }

    @Override
    public void start(TaskPool pool) throws IOException {
        wrapped.start(pool);
    }

}

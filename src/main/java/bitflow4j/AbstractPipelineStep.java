package bitflow4j;

import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.logging.Level;

public abstract class AbstractPipelineStep extends AbstractSource implements PipelineStep {

    protected boolean closed = false;
    protected TaskPool pool;

    @Override
    public String toString() {
        return "a " + getClass().getSimpleName();
    }

    @Override
    public void start(TaskPool pool) throws IOException {
        output(); // Throw exception, if the output field is not set yet
        this.pool = pool;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        output().writeSample(sample);
    }

    protected void doClose() throws IOException {
        // Nothing by default
    }

    @Override
    public synchronized final void close() {
        // Make sure the close call is propagated to the output, even if an exception occurs
        try {
            doClose();
        } catch (IOException e) {
            logger.log(Level.SEVERE, this + ": Failed to close", e);
        }
        if (output != null) {
            output.close();
        }
        closed = true;
        notifyAll();
    }

    @Override
    public synchronized void waitUntilClosed() {
        while (!closed) {
            try {
                wait();
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

}

package bitflow4j.sample;

import bitflow4j.task.TaskPool;

import java.io.IOException;

/**
 * Implements basic functionality for closing the output stream.
 * <p>
 * Created by anton on 4/14/16.
 */
public abstract class AbstractSink implements Sink {

    protected boolean closed = false;

    @Override
    public void start(TaskPool pool) throws IOException {
        // Many sinks have no initialization routines
    }

    public synchronized void close() {
        closed = true;
        this.notifyAll();
    }

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

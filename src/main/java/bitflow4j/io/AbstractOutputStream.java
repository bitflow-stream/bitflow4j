package bitflow4j.io;

import java.io.IOException;

/**
 * Implements basic functionality for closing the output stream.
 * <p>
 * Created by anton on 4/14/16.
 */
public abstract class AbstractOutputStream implements MetricOutputStream {

    protected boolean closed = false;

    public synchronized void close() throws IOException {
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

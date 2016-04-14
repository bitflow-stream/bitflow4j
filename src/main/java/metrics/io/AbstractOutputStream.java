package metrics.io;

import java.io.IOException;

/**
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

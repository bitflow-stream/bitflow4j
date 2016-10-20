package bitflow4j.io;

import bitflow4j.Sample;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by anton on 4/5/16.
 * <p>
 * Should be used by exactly one Thread for readSample() and one Thread for writeSample().
 * Only the Thread the uses writeSample() should call close().
 * Otherwise the synchronization mechanisms must be extended.
 */
public class MetricPipe extends AbstractOutputStream implements MetricInputStream, MetricOutputStream {

    private final BlockingQueue<Sample> values;

    // This is used to wake up the reading Thread when closing the pipe
    private final Sample closedMarker = Sample.newEmptySample();

    // Is set to true when closedMarker
    private boolean closedMarkerWasRead = false;

    public MetricPipe() {
        values = new LinkedBlockingQueue<>();
    }

    public MetricPipe(int bufferSize) {
        values = new ArrayBlockingQueue<>(bufferSize);
    }

    public Sample readSample() throws IOException {
        while (true) {
            try {
                if (closed && values.isEmpty()) {
                    throw new InputStreamClosedException();
                }
                Sample result = values.take();
                if (result == closedMarker) {
                    synchronized (this) {
                        closedMarkerWasRead = true;
                        notifyAll();
                    }
                    throw new InputStreamClosedException();
                }
                return result;
            } catch (InterruptedException exc) {
                // ignore
            }
        }
    }

    public void writeSample(Sample data) throws IOException {
        while (true) {
            try {
                if (closed) {
                    throw new IOException("This MetricPipe is closed");
                }
                values.put(data);
                return;
            } catch (InterruptedException exc) {
                // ignore
            }
        }
    }

    public void close() throws IOException {
        if (closed) return;
        writeSample(closedMarker);
        super.close();
    }

    @Override
    public synchronized void waitUntilClosed() {
        while (!(closedMarkerWasRead && closed)) {
            try {
                wait();
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

}

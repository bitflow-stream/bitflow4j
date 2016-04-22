package metrics.io;

import metrics.Sample;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by anton on 4/11/16.
 * <p>
 * This abstract MetricInputStream aggregates multiple other MetricInputStreams into one.
 * Every incoming Sample is
 */
public abstract class MetricInputAggregator implements MetricInputStream {

    final Set<InputStreamProducer> producers = new HashSet<>();

    public abstract boolean hasRunningInput();

    public abstract int size();

    protected abstract void waitForNewInput();

    protected abstract Sample doReadSample() throws IOException;

    public synchronized void producerStarting(InputStreamProducer producer) {
        producers.add(producer);
    }

    public synchronized void producerFinished(InputStreamProducer producer) {
        producers.remove(producer);
    }

    public abstract void addInput(String name, MetricInputStream input);

    synchronized boolean isClosed() {
        return producers.isEmpty() && !hasRunningInput();
    }

    /**
     * This should only be read from one Thread. Possibly blocks until new input data
     * is available, depending on the subclass implementation.
     */
    public Sample readSample() throws IOException {
        checkClosed();
        waitForNewInput();
        checkClosed();
        return doReadSample();
    }

    void inputFinished(String name, Throwable exception) {
        if (exception != null) {
            System.err.println("Input closed: " + name + ", error: " + exception.getMessage());
            exception.printStackTrace();
        } else {
            System.err.println("Input closed: " + name);
        }
    }

    private void checkClosed() throws InputStreamClosedException {
        if (isClosed()) {
            throw new InputStreamClosedException();
        }
    }

}

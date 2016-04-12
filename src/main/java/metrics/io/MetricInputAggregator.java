package metrics.io;

import metrics.Sample;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by anton on 4/11/16.
 */
public abstract class MetricInputAggregator implements MetricInputStream {

    protected final Set<InputStreamProducer> producers = new HashSet<>();

    protected String unifiedSource = null;

    // Will set Sample.source on every outgoing Sample to unifiedSource.
    public void setUnifiedSampleSource(String unifiedSource) {
        this.unifiedSource = unifiedSource;
    }

    public synchronized void producerStarting(InputStreamProducer producer) {
        producers.add(producer);
    }

    public synchronized void producerFinished(InputStreamProducer producer) {
        producers.remove(producer);
    }

    public abstract void addInput(String name, MetricInputStream input);

    public synchronized boolean isClosed() {
        if (!producers.isEmpty()) return false;
        if (hasRunningInput()) return false;
        return true;
    }

    public abstract boolean hasRunningInput();

    public abstract int size();

    protected abstract void waitForNewInput();

    protected abstract Sample doReadSample() throws IOException;

    protected void inputFinished(String name, Throwable exception) {
        if (exception != null) {
            System.err.println("Input closed: " + name + ", error: " + exception.getMessage());
            exception.printStackTrace();
        } else {
            System.err.println("Input closed: " + name);
        }
    }

    /**
     * This should only be read from one Thread. Possibly blocks until new input data
     * is available, depending on the subclass implementation.
     * The aggregated timestamp will be the one of the latest received sample.
     */
    public Sample readSample() throws IOException {
        checkClosed();
        waitForNewInput();
        checkClosed();
        return doReadSample();
    }

    private void checkClosed() throws InputStreamClosedException {
        if (isClosed()) {
            throw new InputStreamClosedException();
        }
    }

}

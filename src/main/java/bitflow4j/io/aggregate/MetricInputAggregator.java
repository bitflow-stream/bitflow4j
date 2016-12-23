package bitflow4j.io.aggregate;

import bitflow4j.Sample;
import bitflow4j.io.InputStreamClosedException;
import bitflow4j.io.MetricInputStream;
import bitflow4j.main.ParameterHash;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by anton on 4/11/16.
 * <p>
 * This abstract MetricInputStream aggregates multiple other MetricInputStreams into one.
 */
public abstract class MetricInputAggregator implements MetricInputStream {

    private static final Logger logger = Logger.getLogger(MetricInputAggregator.class.getName());

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
            logger.info("Input closed: " + name + ", error: " + exception.getMessage());
            exception.printStackTrace();
        } else {
            logger.info("Input closed: " + name);
        }
    }

    private void checkClosed() throws InputStreamClosedException {
        if (isClosed()) {
            throw new InputStreamClosedException();
        }
    }

}

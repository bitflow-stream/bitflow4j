package bitflow4j.io.aggregate;

import bitflow4j.Header;
import bitflow4j.Sample;
import bitflow4j.io.InputStreamClosedException;
import bitflow4j.io.MetricInputStream;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by anton on 4/6/16.
 * <p>
 * Inputs are read in parallel, independently from readSample(),
 * overwriting intermediate data if readSample() is not called frequently enough.
 * readSample() will only block until the data changes in any way:
 * an input stream delivers data, a new input stream is added, or an input stream is closed.
 */
public abstract class AbstractParallelAggregator extends MetricInputAggregator {

    // Can be set to >0 to allow some errors in the input threads
    private static final int MAX_INPUT_ERRORS = 0;
    static final String HEADER_SEPARATOR = "/";

    // Access to this is synchronized on AbstractParallelAggregator.this
    final Map<String, AggregatingThread> inputs = new TreeMap<>();

    @Override
    public synchronized void producerFinished(InputStreamProducer producer) {
        super.producerFinished(producer);
        if (isClosed()) {
            notifyNewInput(null);
        }
    }

    @Override
    public synchronized void addInput(String name, MetricInputStream input) {
        // Another check like this will be performed inside the thread.
        if (inputs.containsKey(name))
            throw new IllegalStateException("Input with name " + name + " already exists");
        new AggregatingThread(input, name).start();
    }

    public synchronized int size() {
        return inputs.size();
    }

    @Override
    public synchronized boolean hasRunningInput() {
        for (AggregatingThread input : inputs.values())
            if (input.running)
                return true;
        return false;
    }

    void updateHeader(AggregatingThread thread) {
        notifyNewInput(thread);
    }

    abstract void notifyNewInput(AggregatingThread input);

    abstract void receivedNewSample(AggregatingThread thread, Sample sample);

    private synchronized boolean inputStarting(AggregatingThread thread) {
        if (inputs.containsKey(thread.name))
            return false;
        inputs.put(thread.name, thread);
        return true;
    }

    private synchronized void inputFinished(AggregatingThread thread, Throwable exception) {
        inputs.remove(thread.name);
        updateHeader(thread);
        super.inputFinished(thread.name, exception);
    }

    class AggregatingThread extends Thread {

        final MetricInputStream input;
        final String name;
        int errors = 0;
        boolean running = true;

        Sample sample = null;

        // Only used by ParallelAssemblingAggregator
        Header header = null;

        AggregatingThread(MetricInputStream input, String name) {
            this.name = name;
            this.input = input;
            this.setDaemon(false);
        }

        @SuppressWarnings("StatementWithEmptyBody")
        public void run() {
            if (!inputStarting(this))
                throw new IllegalStateException("Input with name " + name + " already exists");
            Throwable exception = null;
            try {
                while (pollSample()) ; // Only ends on exception
            } catch (Throwable t) {
                exception = t;
            } finally {
                running = false;
                inputFinished(this, exception);
            }
        }

        private boolean pollSample() {
            try {
                Sample sample = input.readSample();
                errors = 0;
                receivedNewSample(this, sample);
            } catch (InputStreamClosedException exc) {
                return false;
            } catch (Exception exc) {
                errors++;
                if (errors > MAX_INPUT_ERRORS) {
                    // TODO the input stream should be closed here, need close() method.
                    throw new IllegalStateException("Too many errors in input stream " + name, exc);
                }
            }
            return true;
        }

    }

}

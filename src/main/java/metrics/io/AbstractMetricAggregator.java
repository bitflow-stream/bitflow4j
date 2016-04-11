package metrics.io;

import metrics.Sample;

import java.io.IOException;
import java.util.*;

/**
 * Created by anton on 4/6/16.
 *
 * TODO source and label of incoming Samples are currently discarded.
 */
public abstract class AbstractMetricAggregator implements MetricInputStream {

    private static final int MAX_INPUT_ERRORS = 0;
    private static final String HEADER_SEPARATOR = "/";

    // Access to the following 2 is synchronized on AbstractMetricAggregator.this
    protected final Map<String, AggregatingThread> inputs = new TreeMap<>();
    private final Set<InputStreamProducer> producers = new HashSet<>();

    // Access to the following 3 is synchronized on activeInputs
    // Access to fields in AggregatingThread (header, values, timestamp) is also synchronized on activeInputs
    protected final List<AggregatingThread> activeInputs = new ArrayList<>(); // Subset of inputs that have a valid header
    private ArrayList<String> aggregatedHeaderList = new ArrayList<>();
    private Sample.Header aggregatedHeader;
    private static final int HANDLED_SPECIAL_FIELDS = 1;

    public synchronized void producerStarting(InputStreamProducer producer) {
        producers.add(producer);
    }

    public synchronized void producerFinished(InputStreamProducer producer) {
        producers.remove(producer);
        if (isClosed()) {
            notifyNewInput(null);
        }
    }

    public synchronized void addInput(String name, MetricInputStream input) {
        // Another check like this will be performed inside the thread.
        if (inputs.containsKey(name))
            throw new IllegalStateException("Input with name " + name + " already exists");
        new AggregatingThread(input, name).start();
    }

    public synchronized int size() {
        return inputs.size();
    }

    public synchronized boolean isClosed() {
        if (!producers.isEmpty()) return false;
        for (AggregatingThread input : inputs.values())
            if (input.running)
                return false;
        return true;
    }

    private void checkClosed() throws InputStreamClosedException {
        if (isClosed()) {
            throw new InputStreamClosedException();
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
        synchronized (activeInputs) {
            if (aggregatedHeader == null)
                // TODO this should not happen
                return Sample.newEmptySample();

            // TODO also handle source and label of incoming metrics somehow.
            double[] metrics = new double[aggregatedHeader.header.length];
            Date timestamp = null;
            int i = 0;
            // Iterate in same order as in updateHeader()
            for (AggregatingThread thread : activeInputs) {
                if (timestamp == null || timestamp.before(thread.timestamp)) {
                    timestamp = thread.timestamp;
                }
                for (double value : thread.values) {
                    metrics[i++] = value;
                }
            }
            inputReceived();
            return new Sample(aggregatedHeader, metrics, timestamp);
        }
    }

    private synchronized void updateHeader(AggregatingThread input) {
        synchronized (activeInputs) {
            aggregatedHeaderList.clear();
            activeInputs.clear();
            for (String name : inputs.keySet()) {
                AggregatingThread thread = inputs.get(name);
                if (thread.header != null) {
                    activeInputs.add(thread);
                    for (String headerField : thread.header.header) {
                        aggregatedHeaderList.add(name + HEADER_SEPARATOR + headerField);
                    }
                }
            }
            String aggregated[] = aggregatedHeaderList.toArray(new String[aggregatedHeaderList.size()]);
            aggregatedHeader = new Sample.Header(aggregated, HANDLED_SPECIAL_FIELDS);
            notifyNewInput(input);
        }
    }

    // By implementing this subclasses control the blocking & timing behaviour of all involved Threads
    protected abstract void waitForNewInput(); // May block
    protected abstract void inputReceived(); // Must not block
    protected abstract void inputReady(AggregatingThread input); // May block
    protected abstract void notifyNewInput(AggregatingThread input); // Must not block

    private synchronized boolean inputStarting(AggregatingThread thread) {
        if (inputs.containsKey(thread.name))
            return false;
        inputs.put(thread.name, thread);
        return true;
    }

    private synchronized void inputFinished(AggregatingThread thread, Throwable exception) {
        removeInput(thread);
        if (exception != null) {
            System.err.println("Input closed: " + thread.name + ", error: " + exception.getMessage());
            exception.printStackTrace();
        } else {
            System.err.println("Input closed: " + thread.name);
        }
    }

    protected void removeInput(AggregatingThread thread) {
        inputs.remove(thread.name);
        updateHeader(thread);
    }

    protected class AggregatingThread extends Thread {

        private final MetricInputStream input;
        private final String name;
        private int errors = 0;
        protected boolean running = true;

        private Date timestamp = null;
        private Sample.Header header = null;
        private double[] values = null;

        AggregatingThread(MetricInputStream input, String name) {
            this.name = name;
            this.input = input;
            this.setDaemon(false);
        }

        public void run() {
            if (!inputStarting(this))
                throw new IllegalStateException("Input with name " + name + " already exists");
            Throwable exception = null;
            try {
                while (pollSample()) ;
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
                updateSample(sample);
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

        private void updateSample(Sample sample) {
            inputReady(this);
            synchronized (activeInputs) {
                timestamp = sample.getTimestamp();
                values = sample.getMetrics();
                if (sample.headerChanged(header)) {
                    header = sample.getHeader();
                    updateHeader(this);
                } else {
                    notifyNewInput(this);
                }
            }
        }

    }

}

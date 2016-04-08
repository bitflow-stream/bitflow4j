package MetricIO;

import Metrics.Sample;

import java.io.IOException;
import java.util.*;

/**
 * Created by anton on 4/6/16.
 *
 * TODO Access to the aggregated header and metrics is synchronized on the MetricInputAggregator instance.
 * This could be done more efficiently with Read/Write locks and/or more selective synchronization.
 */
public class MetricInputAggregator implements MetricInputStream {

    private static final int MAX_INPUT_ERRORS = 0;
    private static final String HEADER_SEPARATOR = "/";

    private final SortedMap<String, AggregatingThread> inputs = new TreeMap<>();
    private final List<AggregatingThread> activeInputs = new ArrayList<>(); // Subset of inputs that have a valid header
    private boolean newInput = false;

    private ArrayList<String> aggregatedHeaderList = new ArrayList<>();
    private String[] aggregatedHeader;

    private final Set<InputStreamProducer> producers = new HashSet<>();

    public synchronized void producerStarting(InputStreamProducer producer) {
        producers.add(producer);
    }

    public synchronized void producerFinished(InputStreamProducer producer) {
        producers.remove(producer);
        if (isClosed()) {
            notifyNewInput();
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
        return producers.isEmpty() && inputs.isEmpty();
    }

    private void checkClosed() throws InputStreamClosedException {
        if (isClosed()) {
            throw new InputStreamClosedException();
        }
    }

    /**
     * This should only be read from one Thread. After one read it will block
     * until the data changes in any way: an input stream delivered data, a new
     * input stream is added, or an input stream is closed.
     * The aggregated timestamp will be the one of the latest received sample.
     */
    public Sample readSample() throws IOException {
        checkClosed();
        waitForNewInput();
        synchronized (this) {
            checkClosed();
            Date timestamp = null;
            double[] metrics = new double[aggregatedHeader.length];
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
            return new Sample(aggregatedHeader, timestamp, metrics);
        }
    }

    private synchronized void updateHeader() {
        aggregatedHeaderList.clear();
        activeInputs.clear();
        for (String name : inputs.keySet()) {
            AggregatingThread thread = inputs.get(name);
            if (thread.header != null) {
                activeInputs.add(thread);
                for (String headerField : thread.header) {
                    aggregatedHeaderList.add(name + HEADER_SEPARATOR + headerField);
                }
            }
        }
        aggregatedHeader = aggregatedHeaderList.toArray(new String[aggregatedHeaderList.size()]);
        notifyNewInput();
    }

    private synchronized void waitForNewInput() {
        while (!newInput) {
            try {
                this.wait();
            } catch (InterruptedException e) {
            }
        }
        newInput = false;
    }

    private synchronized void notifyNewInput() {
        newInput = true;
        this.notifyAll();
    }

    private synchronized boolean inputStarting(String name, AggregatingThread thread) {
        if (inputs.containsKey(name))
            return false;
        inputs.put(name, thread);
        return true;
    }

    private synchronized void inputFinished(String name, Throwable exception) {
        inputs.remove(name);
        if (exception != null) {
            System.err.println("Input closed: " + name + ", error: " + exception.getMessage());
            exception.printStackTrace();
        } else {
            System.err.println("Input closed: " + name);
        }
        updateHeader();
    }

    private class AggregatingThread extends Thread {

        private final MetricInputStream input;
        private final String name;
        private int errors = 0;

        private Date timestamp = null;
        private String[] header = null;
        private double[] values = null;

        AggregatingThread(MetricInputStream input, String name) {
            this.name = name;
            this.input = input;
            this.setDaemon(false);
        }

        public void run() {
            if (!inputStarting(name, this))
                throw new IllegalStateException("Input with name " + name + " already exists");
            Throwable exception = null;
            try {
                while (pollSample()) ;
            } catch (Throwable t) {
                exception = t;
            } finally {
                inputFinished(name, exception);
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
                    throw new IllegalStateException("Too many errors in input stream " + name, exc);
                }
            }
            return true;
        }

        private void updateSample(Sample sample) {
            synchronized (MetricInputAggregator.this) {
                timestamp = sample.getTimestamp();
                values = sample.getMetrics();
                if (sample.headerChanged(header)) {
                    header = sample.getHeader();
                    updateHeader();
                } else {
                    notifyNewInput();
                }
            }
        }

    }

}

package metrics.io;

import metrics.Sample;

import java.io.IOException;
import java.util.*;

/**
 * Created by anton on 4/6/16.
 * <p>
 * TODO source and label of incoming Samples are currently discarded.
 */
public abstract class AbstractParallelAggregator extends MetricInputAggregator {

    private static final int MAX_INPUT_ERRORS = 0;
    private static final String HEADER_SEPARATOR = "/";

    // Access to this is synchronized on AbstractParallelAggregator.this
    protected final Map<String, AggregatingThread> inputs = new TreeMap<>();

    // Access to the following 3 is synchronized on activeInputs
    // Access to fields in AggregatingThread (header, values, timestamp) is also synchronized on activeInputs
    protected final List<AggregatingThread> activeInputs = new ArrayList<>(); // Subset of inputs that have a valid header
    private ArrayList<String> aggregatedHeaderList = new ArrayList<>();
    private Sample.Header aggregatedHeader;

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
                return false;
        return true;
    }

    private static String concat(Collection<String> strings) {
        if (strings == null || strings.isEmpty()) return null;
        StringBuilder b = new StringBuilder();
        boolean started = false;
        for (String s : strings) {
            if (s == null) continue;
            if (started)
                b.append("|");
            b.append(s);
            started = true;
        }
        return b.toString();
    }

    public Sample doReadSample() throws IOException {
        synchronized (activeInputs) {
            if (aggregatedHeader == null)
                // TODO this should not happen
                return Sample.newEmptySample();

            // TODO clean up code
            Set<String> labels = aggregatedHeader.hasLabel() ? new HashSet<>() : null;
            Set<String> sources = new HashSet<>();
            double[] metrics = new double[aggregatedHeader.header.length];
            Date timestamp = new Date();
            int i = 0;
            // Iterate in same order as in updateHeader()
            for (AggregatingThread thread : activeInputs) {
                if (timestamp.before(thread.timestamp)) {
                    timestamp = thread.timestamp;
                }
                for (double value : thread.values) {
                    metrics[i++] = value;
                }
                if (labels != null) labels.add(thread.label);
                sources.add(thread.source);
            }
            inputReceived();
            String source = concat(sources);
            String label = concat(labels);
            return new Sample(aggregatedHeader, metrics, timestamp, source, label);
        }
    }

    private synchronized void updateHeader(AggregatingThread input) {
        synchronized (activeInputs) {
            aggregatedHeaderList.clear();
            activeInputs.clear();
            int specialFields = 0;
            for (String name : inputs.keySet()) {
                AggregatingThread thread = inputs.get(name);
                if (thread.header != null) {
                    activeInputs.add(thread);
                    for (String headerField : thread.header.header) {
                        aggregatedHeaderList.add(name + HEADER_SEPARATOR + headerField);
                    }
                    if (thread.header.specialFields > specialFields)
                        specialFields = thread.header.specialFields;
                }
            }
            String aggregated[] = aggregatedHeaderList.toArray(new String[aggregatedHeaderList.size()]);
            aggregatedHeader = new Sample.Header(aggregated, specialFields);
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
        super.inputFinished(thread.name, exception);
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
        private String source = null;
        private String label = null;
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
                source = sample.getSource();
                label = sample.getLabel();
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

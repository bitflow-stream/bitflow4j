package bitflow4j.io.aggregate;

import bitflow4j.Header;
import bitflow4j.Sample;

import java.io.IOException;
import java.util.*;

/**
 * Created by anton on 4/28/16.
 * <p>
 * Incoming Samples from different inputs are assembled into one big Sample.
 * readSample() blocks until one of the inputs delivers a new Sample.
 */
public class ParallelAssemblingAggregator extends AbstractParallelAggregator {

    private ArrayList<String> aggregatedHeaderList = new ArrayList<>();
    private Header aggregatedHeader;

    private final Object newInputLock = new Object();
    private boolean newInput = false;

    // Access to the activeInputs is synchronized on activeInputs
    // Access to fields in AggregatingThread (header, sample) is also synchronized on activeInputs
    private final List<AggregatingThread> activeInputs = new ArrayList<>(); // Subset of inputs that have a valid header

    public Sample doReadSample() throws IOException {
        synchronized (activeInputs) {
            if (aggregatedHeader == null)
                // TODO this should not happen
                return Sample.newEmptySample();

            // TODO clean up code
            Map<String, Set<String>> tags = new HashMap<>();
            double[] metrics = new double[aggregatedHeader.header.length];
            Date timestamp = new Date();
            int i = 0;
            // Iterate in same order as in updateHeader()
            for (AggregatingThread thread : activeInputs) {
                Sample sample = thread.sample;
                if (timestamp.before(sample.getTimestamp())) {
                    timestamp = sample.getTimestamp();
                }
                for (double value : sample.getMetrics()) {
                    metrics[i++] = value;
                }
                Map<String, String> sampleTags = sample.getTags();
                if (sampleTags != null) {
                    for (Map.Entry<String, String> tag : sampleTags.entrySet()) {
                        Set<String> collectedTags = tags.get(tag.getKey());
                        if (collectedTags == null) {
                            collectedTags = new TreeSet<>();
                            tags.put(tag.getKey(), collectedTags);
                        }
                        collectedTags.add(tag.getValue());
                    }
                }
            }
            Map<String, String> tagMap = null;
            if (!tags.isEmpty()) {
                tagMap = new HashMap<>();
                for (Map.Entry<String, Set<String>> tag : tags.entrySet()) {
                    String value = concat(tag.getValue());
                    tagMap.put(tag.getKey(), value);
                }
            }
            return new Sample(aggregatedHeader, metrics, timestamp, tagMap);
        }
    }

    @Override
    synchronized void updateHeader(AggregatingThread input) {
        synchronized (activeInputs) {
            aggregatedHeaderList.clear();
            activeInputs.clear();
            boolean hasTags = false;
            for (String name : inputs.keySet()) {
                AggregatingThread thread = inputs.get(name);
                if (thread.header != null) {
                    activeInputs.add(thread);
                    for (String headerField : thread.header.header) {
                        aggregatedHeaderList.add(name + HEADER_SEPARATOR + headerField);
                    }
                    if (thread.header.hasTags) hasTags = true;
                }
            }
            String aggregated[] = aggregatedHeaderList.toArray(new String[aggregatedHeaderList.size()]);
            aggregatedHeader = new Header(aggregated, hasTags);
            notifyNewInput(input);
        }
    }

    void receivedNewSample(AggregatingThread thread, Sample sample) {
        synchronized (activeInputs) {
            thread.sample = sample;
            if (sample.headerChanged(thread.header)) {
                thread.header = sample.getHeader();
                updateHeader(thread);
            } else {
                notifyNewInput(thread);
            }
        }
    }

    protected void waitForNewInput() {
        synchronized (newInputLock) {
            while (!newInput) {
                try {
                    newInputLock.wait();
                } catch (InterruptedException e) {
                    // ignore
                }
            }
            newInput = false;
        }
    }

    void notifyNewInput(AggregatingThread input) {
        synchronized (newInputLock) {
            newInput = true;
            newInputLock.notifyAll();
        }
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

}

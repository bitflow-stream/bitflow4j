package bitflow4j.window;

import bitflow4j.sample.Header;
import bitflow4j.sample.Sample;

import java.util.*;

/**
 * Created by anton on 5/6/16.
 * <p>
 * Instead of storing all Samples in a queue, every metric is stored in its own MetricWindow
 * separately. This way Samples must be reconstructed, but changes in incoming headers can be tolerated.
 * Also, statistics and calculations can be done on every metric individually.
 */
public class MultiHeaderWindow<T extends MetricWindow> extends AbstractSampleWindow {

    private final MetricWindowFactory<T> factory;
    private final SortedMap<String, T> metrics = new TreeMap<>();
    private final LinkedList<SampleMetadata> samples = new LinkedList<>();
    private int length = 0;

    public MultiHeaderWindow(MetricWindowFactory<T> factory) {
        this.factory = factory;
    }

    public MultiHeaderWindow(int windowSize, MetricWindowFactory<T> factory) {
        super(windowSize);
        this.factory = factory;
    }

    public MultiHeaderWindow(long windowTimespan, MetricWindowFactory<T> factory) {
        super(windowTimespan);
        this.factory = factory;
    }

    @Override
    void flushSamples(int numSamples) {
        length -= numSamples;
        for (int i = 0; i < numSamples; i++)
            samples.poll();
        for (T window : metrics.values())
            window.flushSamples(numSamples);
    }

    @Override
    boolean addSample(Sample sample) {
        boolean changed = false;
        String[] header = sample.getHeader().header;
        Set<String> unhandledStats = new HashSet<>(metrics.keySet());
        double[] values = sample.getMetrics();
        for (int i = 0; i < header.length; i++) {
            if (!metrics.containsKey(header[i]))
                changed = true;
            T metric = getWindow(header[i]);
            metric.add(values[i]);
            unhandledStats.remove(metric.name); // Might not be contained
        }
        samples.add(new SampleMetadata(sample));
        length++;
        for (String unhandledHeader : unhandledStats) {
            metrics.get(unhandledHeader).fill(1);
        }
        return changed;
    }

    @Override
    public Header getHeader() {
        String headerFields[] = new String[metrics.size()];
        int i = 0;
        for (T window : metrics.values()) {
            headerFields[i++] = window.name;
        }
        return new Header(headerFields);
    }

    @Override
    public int numMetrics() {
        return metrics.size();
    }

    public Sample makeSample(int sampleNr) {
        return samples.get(sampleNr).newSample(getHeader(), getSampleValues(sampleNr));
    }

    @Override
    public double[] getSampleValues(int sampleNr) {
        double row[] = new double[metrics.size()];
        int metricNr = 0;
        for (MetricWindow window : metrics.values()) {
            row[metricNr] = window.getValue(sampleNr);
            metricNr++;
        }
        return row;
    }

    public T getWindow(String name) {
        T result;
        if ((result = metrics.get(name)) == null) {
            result = factory.newMetricWindow(name);
            result.fill(length); // Fill up missed values
            metrics.put(name, result);
        }
        return result;
    }

    public Collection<T> allMetricWindows() {
        return metrics.values();
    }

    public Collection<String> allMetricNames() {
        return metrics.keySet();
    }

    @Override
    public void clear() {
        super.clear();
        for (T window : metrics.values()) {
            window.clear();
        }
        length = 0;
        samples.clear();
        metrics.clear();
    }

    @Override
    public SampleMetadata getSampleMetadata(int sampleNr) {
        return samples.get(sampleNr);
    }

}

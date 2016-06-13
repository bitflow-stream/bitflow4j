package metrics.io.window;

import metrics.Header;
import metrics.Sample;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by anton on 5/9/16.
 * <p>
 * Sample window that is automatically sorted by the timestamps of incoming Samples.
 * The 'long' constructor is disabled because this subclass disturbs ordering of the Samples
 */
public class SortedWindow extends AbstractSampleWindow {

    public SortedWindow(AbstractSampleWindow backingWindow) {
        this.backingWindow = backingWindow;
    }

    public SortedWindow(boolean changingHeaders) {
        this(changingHeaders ? new MultiHeaderWindow<>(MetricWindow.FACTORY) : new SampleWindow());
    }

    public SortedWindow() {
        this(false);
    }

    private final AbstractSampleWindow backingWindow;
    public final List<SampleDate> index = new LinkedList<>();

    @Override
    public boolean addSample(Sample sample) {
        boolean result = backingWindow.add(sample);
        index.add(new SampleDate(backingWindow.numSamples()-1, sample.getTimestamp()));
        Collections.sort(index);
        return result;
    }

    @Override
    public Header getHeader() {
        return backingWindow.getHeader();
    }

    @Override
    public int numMetrics() {
        return backingWindow.numMetrics();
    }

    @Override
    void flushSamples(int numSamples) {
        if (numSamples < backingWindow.numSamples()) {
            throw new UnsupportedOperationException("SortedWindow does not support flushing");
        }
        clear();
    }

    @Override
    public void clear() {
        super.clear();
        backingWindow.clear();
        index.clear();
    }

    @Override
    public double[] getSampleValues(int sampleNum) {
        SampleDate date = index.get(sampleNum);
        return backingWindow.getSampleValues(date.index);
    }

    @Override
    public SampleMetadata getSampleMetadata(int sampleNum) {
        SampleDate date = index.get(sampleNum);
        return backingWindow.getSampleMetadata(date.index);
    }
    
    @Override
    public int numSamples() {
        return backingWindow.numSamples();
    }

    private static class SampleDate implements Comparable<SampleDate> {
        final Date timestamp;
        final int index;

        SampleDate(int index, Date timestamp) {
            this.timestamp = timestamp;
            this.index = index;
        }

        @Override
        public int compareTo(SampleDate o) {
            if (timestamp == null && o.timestamp == null) return 0;
            if (timestamp == null) return -1;
            if (o.timestamp == null) return 1;
            return timestamp.compareTo(o.timestamp);
        }

    }

}

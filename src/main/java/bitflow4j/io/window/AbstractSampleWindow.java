package bitflow4j.io.window;

import bitflow4j.Header;
import bitflow4j.Sample;

import java.util.Date;
import java.util.LinkedList;

/**
 * Implements a FIFO queue of Samples, optionally automatically flushing
 * Samples after a given number or time span is stored.
 *
 * Created by anton on 5/6/16.
 */
public abstract class AbstractSampleWindow {

    private final int windowSize;
    private final long windowTimespan;

    private LinkedList<Date> timestamps = new LinkedList<>();
    private Date lastTimestamp = null;
    private boolean liveTime = false;

    public AbstractSampleWindow() {
        this.windowSize = -1;
        this.windowTimespan = -1;
    }

    public AbstractSampleWindow(int windowSize) {
        this.windowSize = windowSize;
        this.windowTimespan = -1;
    }

    public AbstractSampleWindow(long windowTimespan) {
        this.windowTimespan = windowTimespan;
        this.windowSize = -1;
    }

    public void useLiveTime() {
        liveTime = true;
    }

    abstract void flushSamples(int numSamples);

    // Return true if getHeader() would return a new header afterwards
    abstract boolean addSample(Sample sample);

    public abstract Header getHeader();

    public final boolean add(Sample sample) {
        boolean result = addSample(sample);
        Date timestamp = sample.getTimestamp();
        timestamps.offer(timestamp);
        if (timestamp != null)
            lastTimestamp = timestamp;
        autoFlush();
        return result;
    }

    private void autoFlush() {
        int flushNum = 0;
        if (windowSize > 0) {
            while (timestamps.size() > windowSize) {
                timestamps.poll();
                flushNum++;
            }
        }
        if (windowTimespan > 0) {
            Date current = currentTime();
            if (current != null) {
                while (true) {
                    Date lastTimestamp = timestamps.peekFirst();
                    long diff = current.getTime() - lastTimestamp.getTime();
                    // Also flush bogus "future" timestamps to avoid hanging on them.
                    if (diff < 0 || diff > windowTimespan) {
                        timestamps.poll();
                        flushNum++;
                    } else {
                        break;
                    }
                }

            }
        }
        if (flushNum > 0) flushSamples(flushNum);
    }

    private Date currentTime() {
        if (liveTime) {
            return new Date();
        } else {
            return lastTimestamp;
        }
    }

    public SampleMetadata getFirst() {
        return getSampleMetadata(0);
    }

    public SampleMetadata getLast() {
        return getSampleMetadata(numSamples());
    }

    public Sample getSample(int sampleNr) {
        return getSampleMetadata(sampleNr).newSample(getHeader(), getSampleValues(sampleNr));
    }

    public abstract double[] getSampleValues(int sampleNum);

    public abstract SampleMetadata getSampleMetadata(int sampleNum);

    public int numSamples() {
        return timestamps.size();
    }

    public void clear() {
        timestamps.clear();
    }

    public abstract int numMetrics();

}

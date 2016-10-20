package bitflow4j.io.window;

import bitflow4j.Header;
import bitflow4j.Sample;

import java.util.LinkedList;

/**
 * A window of samples that simply inserts/outputs the Samples as they come in, without
 * modifying the Samples or order of the Samples.
 *
 * Created by anton on 5/6/16.
 */
public class SampleWindow extends AbstractSampleWindow {

    public SampleWindow() {
    }

    public SampleWindow(int windowSize) {
        super(windowSize);
    }

    public SampleWindow(long windowTimespan) {
        super(windowTimespan);
    }

    private Header header = null;
    public final LinkedList<Sample> samples = new LinkedList<>();

    @Override
    boolean addSample(Sample sample) {
        boolean changed = false;
        if (header == null) {
            header = sample.getHeader();
            changed = true;
        } else if (sample.headerChanged(header))
            throw new IllegalStateException("Changing headers not expected in SampleWindow");
        samples.offer(sample);
        return changed;
    }

    @Override
    public Header getHeader() {
        if (samples.isEmpty()) return Header.EMPTY_HEADER;
        Header header = samples.peek().getHeader();
        return new Header(header.header);
    }

    @Override
    public int numMetrics() {
        if (samples.isEmpty()) return 0;
        return samples.peek().getHeader().header.length;
    }

    @Override
    void flushSamples(int numSamples) {
        for (int i = 0; i < numSamples; i++) {
            samples.poll();
        }
    }

    @Override
    public void clear() {
        super.clear();
        samples.clear();
    }

    @Override
    public Sample getSample(int sampleNr) {
        return samples.get(sampleNr);
    }

    @Override
    public double[] getSampleValues(int sampleNum) {
        return samples.get(sampleNum).getMetrics();
    }

    @Override
    public SampleMetadata getSampleMetadata(int sampleNum) {
        return new SampleMetadata(samples.get(sampleNum));
    }

}

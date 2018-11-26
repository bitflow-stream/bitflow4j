package bitflow4j.steps.metrics;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Header;
import bitflow4j.Sample;

import java.io.IOException;

/**
 * Simple wrapper around the {@link SampleConverger}. Use this pipeline step to push changing headers.
 */
public class SampleConvergerPipelineStep extends AbstractPipelineStep {

    /**
     * Sample converger used to push changing headers.
     */
    private SampleConverger converger; // No predefined expected header

    public SampleConvergerPipelineStep(Header expectedHeader) {
        this.converger = new SampleConverger(expectedHeader);
    }

    public SampleConvergerPipelineStep() {
        this(null);
    }

    @Override
    public synchronized void writeSample(Sample sample) throws IOException {

        double metrics[] = converger.getValues(sample);
        Header header = converger.getExpectedHeader();

        output.writeSample(new Sample(header, metrics, sample));
    }
}

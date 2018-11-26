package bitflow4j.steps.filter;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;

import java.io.IOException;

/**
 * Samples can be filtered out based on some user defined rule.
 * <p>
 * Created by anton on 4/23/16.
 */
public class SampleFilter extends AbstractPipelineStep {

    private final Filter filter;

    public SampleFilter(Filter filter) {
        this.filter = filter;
    }

    public void writeSample(Sample sample) throws IOException {
        if (filter.shouldInclude(sample)) {
            output.writeSample(sample);
        }
    }

    public interface Filter {
        boolean shouldInclude(Sample sample);
    }

}

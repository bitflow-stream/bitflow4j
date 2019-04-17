package bitflow4j.steps.metrics;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Header;
import bitflow4j.Sample;
import bitflow4j.script.registry.Description;

import java.io.IOException;

@Description("Removes all metrics of passed through samples.")
public class Strip extends AbstractPipelineStep {

    public Strip (){}

    @Override
    public void writeSample(Sample sample) throws IOException {
        output.writeSample(new Sample(Header.newEmptyHeader(), new double[0], sample));
    }

}

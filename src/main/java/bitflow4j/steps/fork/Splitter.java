package bitflow4j.steps.fork;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.PipelineStep;
import bitflow4j.Sample;

import java.io.IOException;

/**
 * Created by anton on 02.01.17.
 */
public class Splitter extends AbstractPipelineStep {

    private final PipelineStep extraOutputs[];

    public Splitter(PipelineStep... extraOutputs) {
        this.extraOutputs = extraOutputs;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        if (output != null) {
            output.writeSample(sample);
        }
        for (PipelineStep extraOutput : extraOutputs) {
            extraOutput.writeSample(sample);
        }
    }

}

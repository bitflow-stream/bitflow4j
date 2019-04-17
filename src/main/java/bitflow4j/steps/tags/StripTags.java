package bitflow4j.steps.tags;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;
import bitflow4j.script.registry.Description;

import java.io.IOException;
import java.util.HashMap;

@Description("Removes all tags of passed through samples.")
public class StripTags extends AbstractPipelineStep {

    @Override
    public void writeSample(Sample sample) throws IOException {
        sample.setAllTags(new HashMap<>());
        output.writeSample(sample);
    }

}

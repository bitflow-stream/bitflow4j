package bitflow4j.steps.tags;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;

import java.io.IOException;
import java.util.Map;

/**
 * @author fschmidt
 */
public class SetTags extends AbstractPipelineStep {

    private final Map<String, String> tags;

    public SetTags(Map<String, String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return String.format("Set tags: %s", tags);
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        tags.forEach(sample::setTag);
        super.writeSample(sample);
    }

}

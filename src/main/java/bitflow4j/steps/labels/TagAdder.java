package bitflow4j.steps.labels;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;

import java.io.IOException;
import java.util.Map;

/**
 *
 * @author fschmidt
 */
public class TagAdder extends AbstractPipelineStep {

    private final Map<String, String> tags;

    public TagAdder(Map<String, String> tags) {
        this.tags = tags;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        tags.entrySet()
                .forEach((tag) -> {
                    sample.setTag(tag.getKey(), tag.getValue());
                });
        output.writeSample(sample);
    }

}

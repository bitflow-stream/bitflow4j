package bitflow4j.steps;

import bitflow4j.AbstractProcessingStep;
import bitflow4j.Sample;
import bitflow4j.registry.Description;
import bitflow4j.registry.StepName;

import java.io.IOException;

/**
 * Created by anton on 4/6/16.
 * <p>
 * ProcessingStep doing nothing but forwarding received samples unchanged.
 */
@Description("")
@StepName("noop")
public class NoopStep extends AbstractProcessingStep {

    @Override
    public void handleSample(Sample sample) throws IOException {
        output(sample);
    }

}

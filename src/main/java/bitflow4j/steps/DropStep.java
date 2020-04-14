package bitflow4j.steps;

import bitflow4j.AbstractProcessingStep;
import bitflow4j.registry.Description;
import bitflow4j.registry.StepName;

/**
 * Created by anton on 23/11/18.
 * <p>
 * This ProcessingStep drops incoming samples.
 */
@Description("Drops all samples, which means they are not forwarded.")
@StepName("drop")
public class DropStep extends AbstractProcessingStep {
    // No overridden methods necessary
}

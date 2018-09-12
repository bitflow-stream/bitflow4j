package bitflow4j.main.registry;

import bitflow4j.steps.PipelineStep;

import java.util.Map;

/**
 * StepConstructor defines a method to apply a step on a pipeline using the provided parameters.
 */
@FunctionalInterface
public interface StepConstructor {
    PipelineStep constructPipelineStep(Map<String, String> parameters) throws StepConstructionException;
}

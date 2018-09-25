package bitflow4j.main.registry;

import bitflow4j.steps.PipelineStep;

import java.util.Map;

/**
 * StepConstructor defines a method to create a PipelineStep from provided parameters.
 */
@FunctionalInterface
public interface StepConstructor {
    PipelineStep constructPipelineStep(Map<String, String> parameters) throws StepConstructionException;
}

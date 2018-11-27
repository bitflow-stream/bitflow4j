package bitflow4j.script.registry;

import bitflow4j.PipelineStep;

import java.util.Map;

/**
 * StepConstructor defines a method to create a PipelineStep from provided parameters.
 */
@FunctionalInterface
public interface StepConstructor {
    PipelineStep constructPipelineStep(Map<String, String> parameters) throws StepConstructionException;
}

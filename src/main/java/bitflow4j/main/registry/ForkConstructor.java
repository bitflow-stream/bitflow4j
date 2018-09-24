package bitflow4j.main.registry;

import bitflow4j.main.Pipeline;
import bitflow4j.steps.PipelineStep;
import bitflow4j.steps.fork.Fork;

import java.util.Map;

/**
 * StepConstructor defines a method to apply a step on a pipeline using the provided parameters.
 */
@FunctionalInterface
public interface ForkConstructor {
    Fork constructForkStep(Map<String, Pipeline> subpipelines, Map<String, String> parameters) throws StepConstructionException;
}

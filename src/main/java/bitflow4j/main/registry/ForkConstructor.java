package bitflow4j.main.registry;

import bitflow4j.main.Pipeline;
import bitflow4j.steps.PipelineStep;
import bitflow4j.steps.fork.Fork;

import java.util.Map;

/**
 *  ForkConstructor defines a method to create a Fork from provided parameters and subpipelines.
 */
@FunctionalInterface
public interface ForkConstructor {
    Fork constructForkStep(Map<String, Pipeline> subpipelines, Map<String, String> parameters) throws StepConstructionException;
}

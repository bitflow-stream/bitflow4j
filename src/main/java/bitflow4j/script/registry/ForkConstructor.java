package bitflow4j.script.registry;

import bitflow4j.Pipeline;
import bitflow4j.steps.fork.Fork;

import java.util.Map;

/**
 * ForkConstructor defines a method to create a Fork from provided parameters and sub-pipelines.
 */
@FunctionalInterface
public interface ForkConstructor {

    Fork constructForkStep(Map<String, Pipeline> subPipelines, Map<String, String> parameters) throws StepConstructionException;

}

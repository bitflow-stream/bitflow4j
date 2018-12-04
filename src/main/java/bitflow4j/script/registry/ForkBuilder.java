package bitflow4j.script.registry;

import bitflow4j.steps.fork.ScriptableDistributor;

import java.io.IOException;

/**
 * ForkBuilder defines a method to create a Fork from provided parameters and sub-pipelines.
 */
public interface ForkBuilder {

    ScriptableDistributor buildFork() throws IOException;

}

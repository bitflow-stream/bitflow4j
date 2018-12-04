package bitflow4j.script.registry;

import bitflow4j.Pipeline;
import bitflow4j.misc.Pair;

import java.util.Collection;
import java.util.Map;

/**
 * RegisteredFork Meta information about a fork and a method to generate it from parameters.
 */
public abstract class RegisteredFork extends AbstractRegisteredStep {

    public RegisteredFork(String name) {
        super(name);
    }

    public abstract void buildFork(Pipeline pipeline, Collection<Pair<String, Pipeline>> subPipelines, Map<String, String> parameters) throws ConstructionException;

}

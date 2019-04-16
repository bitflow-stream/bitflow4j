package bitflow4j.script.registry;

import bitflow4j.Pipeline;

import java.util.Map;

/**
 * RegisteredPipelineStep defines an analysis step, a function to apply it on a pipeline and the options to that function.
 */
public abstract class RegisteredPipelineStep extends AbstractRegisteredStep {

    public RegisteredPipelineStep(String className, String description) {
        super(className, description);
    }

    public abstract void buildStep(Pipeline pipeline, Map<String, String> parameters) throws ConstructionException;

}

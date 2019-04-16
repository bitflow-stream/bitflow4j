package bitflow4j.script.registry;

import bitflow4j.steps.BatchHandler;

import java.util.Map;

/**
 * RegisteredBatchStep defines an analysis step that can be used inside the batch() {...} environment.
 */
public abstract class RegisteredBatchStep extends AbstractRegisteredStep {

    public RegisteredBatchStep(String className, String description) {
        super(className, description);
    }

    public abstract BatchHandler buildStep(Map<String, String> parameters) throws ConstructionException;

}

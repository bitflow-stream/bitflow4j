package bitflow4j.script.registry;

import bitflow4j.Pipeline;

import java.util.Map;

/**
 * RegisteredPipelineStep defines an analysis step, a function to apply it on a pipeline and the options to that function.
 */
public abstract class RegisteredPipelineStep extends AbstractRegisteredStep {

    private boolean _supportsBatchProcessing;
    private boolean _supportsStreamProcessing;

    public RegisteredPipelineStep(String name) {
        super(name);
    }

    public abstract void buildStep(Pipeline pipeline, Map<String, String> parameters) throws ConstructionException;

    public RegisteredPipelineStep supportBatch() {
        _supportsBatchProcessing = true;
        return this;
    }

    public RegisteredPipelineStep supportStream() {
        _supportsStreamProcessing = true;
        return this;
    }

    public boolean supportsBatch() {
        return _supportsBatchProcessing;
    }

    public boolean supportsStream() {
        return _supportsStreamProcessing;
    }

    public boolean supportsBatchOnly() {
        return _supportsBatchProcessing && !_supportsStreamProcessing;
    }

    public boolean supportsStreamOnly() {
        return _supportsStreamProcessing && !_supportsBatchProcessing;
    }

    public boolean supportsBothModes() {
        return _supportsStreamProcessing && _supportsBatchProcessing;
    }

}

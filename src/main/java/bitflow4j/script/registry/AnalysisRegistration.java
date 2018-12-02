package bitflow4j.script.registry;

/**
 * AnalsisRegistration defines an analysis step, a function to apply it on a pipeline and the options to that function.
 */
public class AnalysisRegistration extends Registration {

    private boolean _supportsBatchProcessing;
    private boolean _supportsStreamProcessing;
    private final StepConstructor stepConstructor;

    public AnalysisRegistration(String name, StepConstructor constructor) {
        super(name);
        this.stepConstructor = constructor;
    }

    public AnalysisRegistration supportBatch() {
        _supportsBatchProcessing = true;
        return this;
    }

    public AnalysisRegistration supportStream() {
        _supportsStreamProcessing = true;
        return this;
    }

    public StepConstructor getStepConstructor() {
        return stepConstructor;
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

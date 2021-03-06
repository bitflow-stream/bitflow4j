package bitflow4j.registry;

/**
 * ConstructionException represents an Exception occurred during construction of a PipelineStep, it contains
 * the name of the step and the original error message.
 */
public class ConstructionException extends Exception {

    private final String stepName;

    public ConstructionException(String stepName, String message) {
        super(message);
        this.stepName = stepName;
    }

    public ConstructionException(String stepName, Throwable cause) {
        super(cause);
        this.stepName = stepName;
    }

    public ConstructionException(String stepName, String message, Throwable cause) {
        super(message, cause);
        this.stepName = stepName;
    }

    public String getStepName() {
        return stepName;
    }

}

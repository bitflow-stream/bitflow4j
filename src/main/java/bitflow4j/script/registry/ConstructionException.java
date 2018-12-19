package bitflow4j.script.registry;

/**
 * ConstructionException represents an Exception occurred during construction of a PipelineStep, it contains
 * the name of the step and the original error message.
 */
public class ConstructionException extends Exception {

    private String stepName;

    public ConstructionException(String stepName, String message) {
        super(message);
        this.stepName = stepName;
    }

    public String getStepName() {
        return stepName;
    }

}

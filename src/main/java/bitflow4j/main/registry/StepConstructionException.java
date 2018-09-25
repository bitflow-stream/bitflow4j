package bitflow4j.main.registry;

/**
 * StepConstructionException represents an Exception occurred during construction of a PipelineStep, it contains
 * the name of the step and the original error message.
 */
public class StepConstructionException extends Exception {

    private String stepName;

    public StepConstructionException(String stepName, String message) {
        super(message);
        this.stepName = stepName;
    }

    public String getStepName() {
        return stepName;
    }
}
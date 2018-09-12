package bitflow4j.main.registry;

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
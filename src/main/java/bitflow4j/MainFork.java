package bitflow4j;

import bitflow4j.steps.ForkedStep;

import java.io.IOException;
import java.util.function.Supplier;

public class MainFork extends ForkedStep {

    private final Supplier<ProcessingStep> stepFactory;

    public MainFork(String tag, Supplier<ProcessingStep> stepFactory) {
        super(tag);
        this.stepFactory = stepFactory;
    }

    @Override
    protected ForkStepHandler createForkHandler(String forkTagValue) throws IOException {
        logger.info(String.format("Creating step instance for %s=%s", forkTag, forkTagValue));
        ProcessingStep step = stepFactory.get();
        if (step == null)
            return null;
        step.initialize(context);
        return new StepWrapper(step);
    }

    private static class StepWrapper implements ForkStepHandler {

        private final ProcessingStep step;

        private StepWrapper(ProcessingStep step) {
            this.step = step;
        }

        @Override
        public void handleSample(Sample sample) throws IOException {
            step.handleSample(sample);
        }

        @Override
        public void cleanup() throws IOException {
            step.cleanup();
        }
    }

}

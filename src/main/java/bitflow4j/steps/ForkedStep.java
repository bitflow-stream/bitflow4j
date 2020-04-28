package bitflow4j.steps;

import bitflow4j.AbstractProcessingStep;
import bitflow4j.Sample;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class ForkedStep extends AbstractProcessingStep {

    public final String forkTag;
    private boolean warnedMissingTags = false;
    private final Map<String, ForkStepHandler> handlers = new HashMap<>();

    public ForkedStep(String tag) {
        this.forkTag = tag;
    }

    public void handleSample(Sample sample) throws IOException {
        String tagValue = sample.getTag(forkTag);
        if (tagValue == null || tagValue.isEmpty()) {
            if (!warnedMissingTags) {
                logger.warning(String.format("Dropping samples without %s tag", forkTag));
                warnedMissingTags = true;
            }
            return;
        }
        getForkHandler(tagValue).handleSample(sample);
    }

    public ForkStepHandler getForkHandler(String tagValue) throws IOException {
        ForkStepHandler handler = handlers.get(tagValue);
        if (handler == null) {
            handler = createForkHandler(tagValue);
            handlers.put(tagValue, handler);
        }
        return handler;
    }

    protected abstract ForkStepHandler createForkHandler(String forkTagValue) throws IOException;

    @Override
    public void cleanup() throws IOException {
        for (ForkStepHandler handler : handlers.values()) {
            handler.cleanup();
        }
        super.cleanup();
    }

    public interface ForkStepHandler {

        void handleSample(Sample sample) throws IOException;

        default void cleanup() throws IOException {
            // No cleanup by default
        }

    }

}

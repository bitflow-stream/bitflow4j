package bitflow4j.steps;

import bitflow4j.sample.Sink;
import bitflow4j.sample.Source;

import java.util.logging.Logger;

/**
 * An Algorithm receives metrics like a Sink and forwards results
 * to another Sink.
 * The way it does this is completely open: many (or even all) samples can be received
 * before outputting anything, or every sample can be modified and forwarded independently.
 */
public interface PipelineStep extends Source, Sink {

    Logger logger = Logger.getLogger(PipelineStep.class.getName());

    default Object getModel() {
        // Default implementation: we have no model.
        logger.warning("getModel() called for " + getClass() + ". Model is returned as 'null'.");
        return null;
    }

    default void setModel(Object model) {
        // Default implementation: nothing to do. Should usually not occur.
        logger.warning("setModel() called for " + getClass() + ", but is ignored. Model: " + model);
    }

    default void stop() {
        // Algorithms should only be stopped through the close() method of Sink
        throw new UnsupportedOperationException("stop() should not be called on implementations of Algorithm");
    }

}

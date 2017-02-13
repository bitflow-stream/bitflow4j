package bitflow4j.algorithms;

import bitflow4j.sample.SampleSink;
import bitflow4j.sample.SampleSource;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * An Algorithm receives metrics like a SampleSink and forwards results
 * to another SampleSink.
 * The way it does this is completely open: many (or even all) samples can be received
 * before outputting anything, or every sample can be modified and forwarded independently.
 */
public interface Algorithm<T> extends SampleSource, SampleSink {

    Logger logger = Logger.getLogger(Algorithm.class.getName());

    default T getModel() {
        // Default implementation: we have no model.
        logger.warning("getModel() called for " + getClass() + ". Model is returned as 'null'.");
        return null;
    }

    default void setModel(T model) {
        // Default implementation: nothing to do. Should usually not occur.
        logger.warning("setModel() called for " + getClass() + ", but is ignored. Model: " + model);
    }

    default void stop() throws IOException {
        // Algorithms should only be stopped through the close() method of SampleSink
        throw new UnsupportedOperationException("stop() should not be called on implementations of Algorithm");
    }

}

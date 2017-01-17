package bitflow4j.algorithms;

import bitflow4j.io.MetricOutputStream;

import java.util.logging.Logger;

/**
 * An Algorithm receives metrics like a MetricOutputStream and forwards results
 * to another MetricOutputStream.
 * The way it does this is completely open: many (or even all) samples can be received
 * before outputting anything, or every sample can be modified and forwarded independently.
 */
public interface Algorithm<T> extends MetricOutputStream, AutoCloseable {

    Logger logger = Logger.getLogger(Algorithm.class.getName());

    void setOutput(MetricOutputStream output);

    default T getModel() {
        // Default implementation: we have no model.
        logger.warning("getModel() called for " + getClass() + ". Model is returned as 'null'.");
        return null;
    }

    default void setModel(T model) {
        // Default implementation: nothing to do. Should usually not occur.
        logger.warning("setModel() called for " + getClass() + ", but is ignored. Model: " + model);
    }

}

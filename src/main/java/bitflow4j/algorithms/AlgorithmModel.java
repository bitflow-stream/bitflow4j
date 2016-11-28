package bitflow4j.algorithms;

import java.io.Serializable;

/**
 * Basic interface for the underlying model of an algorithm.
 */
public interface AlgorithmModel<T> extends Serializable {

    T getModel();

}

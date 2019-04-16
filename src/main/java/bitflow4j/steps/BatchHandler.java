package bitflow4j.steps;

import bitflow4j.Sample;

import java.io.IOException;
import java.util.List;

/**
 * A BatchHandler receives a list of samples as input, modifies it, and produces a list of output samples.
 * Non-abstract implementations of this will be automatically added to the Registry and can be used in the batch(){} environment
 * in bitflow scripts.
 */
public interface BatchHandler {

    /**
     * This method must handle the incoming batch of samples and produce a result batch.
     * The resulting list can be the same as the input list, which is convenient e.g. when adjust every Sample instance in-place.
     * The resulting list can also be an entirely new list of new Sample instances, and with a new number of elements.
     */
    List<Sample> handleBatch(List<Sample> batch) throws IOException;

}

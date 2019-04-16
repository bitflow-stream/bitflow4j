package bitflow4j.steps.reorder;

import bitflow4j.Sample;
import bitflow4j.steps.BatchHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Shuffles all incoming Samples in batch mode.
 */
public class Shuffle implements BatchHandler {

    @Override
    public List<Sample> handleBatch(List<Sample> window) throws IOException {
        Collections.shuffle(window);
        return window;
    }

}

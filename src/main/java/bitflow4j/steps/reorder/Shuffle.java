package bitflow4j.steps.reorder;

import bitflow4j.Sample;
import bitflow4j.steps.BatchPipelineStep;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Shuffles all incoming Samples in batch mode.
 */
public class Shuffle extends BatchPipelineStep {

    @Override
    protected void flush(List<Sample> window) throws IOException {
        Collections.shuffle(window);
        for (Sample sample : window) {
            output.writeSample(sample);
        }
    }

}

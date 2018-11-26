package bitflow4j.steps.reorder;

import bitflow4j.Sample;
import bitflow4j.steps.batch.BatchPipelineStep;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Shuffles all incomming Samples in batch mode.
 */
public class Shuffler extends BatchPipelineStep {
    List<Sample> samples = new ArrayList<>();

    @Override
    protected void flushAndClearResults() throws IOException {
        Collections.shuffle(samples);
        for (Sample sample : samples) {
            output.writeSample(sample);
        }
        samples = null;
    }

    @Override
    protected void addSample(Sample sample) {
        samples.add(sample);
    }
}

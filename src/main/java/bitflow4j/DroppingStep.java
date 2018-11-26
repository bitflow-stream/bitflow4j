package bitflow4j;

import bitflow4j.task.TaskPool;

import java.io.IOException;

/**
 * Created by anton on 23/11/18.
 * <p>
 * This PipelineStep drops incoming samples.
 */
public class DroppingStep extends AbstractPipelineStep {

    @Override
    public void start(TaskPool pool) throws IOException {
        // No initialization
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        // Do not forward the sample
    }

}

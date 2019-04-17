package bitflow4j;

import bitflow4j.script.registry.Description;
import bitflow4j.task.TaskPool;

import java.io.IOException;

/**
 * Created by anton on 23/11/18.
 * <p>
 * This PipelineStep drops incoming samples.
 */
@Description("Drops all samples, which means they are not forwarded.")
public class DropStep extends AbstractPipelineStep {

    @Override
    public void start(TaskPool pool) throws IOException {
        // No initialization
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        // Do not forward the sample
    }

}

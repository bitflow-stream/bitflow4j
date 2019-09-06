package bitflow4j;

import bitflow4j.task.StoppableLoopTask;
import bitflow4j.task.TaskPool;

import java.io.IOException;

/**
 * Subclassing this abstract class allows to create custom sample sources.
 * In addition, an implementation of CustomSourceFactory is required to use the custom sample source in a Bitflow script.
 * See CustomSourceFactory for more details.
 *
 * @author kevinstyp
 */
public abstract class CustomSource extends AbstractSource {

    private SourceTask task;

    public CustomSource() {
        this.task = new SourceTask();
    }

    @Override
    public void start(TaskPool pool) throws IOException {
        pool.start(task);
    }

    @Override
    public abstract String toString();

    /**
     * Generate or read the next sample. Return null to terminate this source.
     */
    public abstract Sample nextSample();

    private class SourceTask extends StoppableLoopTask {

        @Override
        protected boolean executeIteration() throws IOException {
            if (!pool.isRunning())
                return false;

            Sample sample = nextSample();

            if (sample == null || !pool.isRunning()) {
                return false;
            }
            output().writeSample(sample);
            return true;
        }

        @Override
        public void run() throws IOException {
            super.run();
            // Guarantee that the pipeline is closed
            output().close();
        }

    }

}

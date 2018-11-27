package bitflow4j.io;

import bitflow4j.AbstractSource;
import bitflow4j.PipelineStep;
import bitflow4j.Sample;
import bitflow4j.task.LoopTask;
import bitflow4j.task.ParallelTask;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for implementing Source in case multiple threads
 * are needed, for example when reading from TCP connections or files.
 * <p>
 * Created by anton on 23.12.16.
 */
public abstract class ThreadedSource extends AbstractSource implements ParallelTask {

    private static final Logger logger = Logger.getLogger(ThreadedSource.class.getName());

    protected TaskPool pool;
    protected final Object outputLock = new Object();
    private final List<LoopTask> tasks = new ArrayList<>();
    private boolean initializingReaders = true;

    @Override
    public void start(TaskPool pool) throws IOException {
        this.pool = pool;
    }

    public interface SampleGenerator {
        Sample nextSample() throws IOException;
    }

    public void readSamples(SampleGenerator generator) throws IOException {
        readSamples(generator, false);
    }

    public void readSamples(SampleGenerator generator, boolean backgroundTask) throws IOException {
        readerTask(new LoopSampleReaderTask(generator), backgroundTask);
    }

    public void readerTask(LoopTask task) throws IOException {
        readerTask(task, false);
    }

    public void readerTask(LoopTask task, boolean backgroundTask) throws IOException {
        tasks.add(task);
        pool.start(task, backgroundTask);
    }

    @Override
    public void run() throws IOException {
        // Wait for initialization of all readers, then wait for all readers to exit.
        // Then start the close() sequence with our direct output.
        synchronized (this) {
            while (initializingReaders)
                try {
                    wait();
                } catch (InterruptedException ignored) {
                }
        }
        tasks.forEach(LoopTask::waitForExit);
        output().close();
    }

    // initFinished() should be called in an overridden run() or start() method, after all sub-tasks have been started.
    protected void initFinished() {
        synchronized (this) {
            initializingReaders = false;
            notifyAll();
        }
    }

    public void close() {
        initFinished();
        tasks.forEach(LoopTask::stop);
        // The output is closed in run(), after all tasks finish.
    }

    protected boolean fatalReaderExceptions() {
        // By default, do not shut down when an Exception occurs, keep going until the user shuts us down.
        return false;
    }

    private class LoopSampleReaderTask extends LoopTask {

        private final SampleGenerator generator;
        private final PipelineStep sink;

        public LoopSampleReaderTask(SampleGenerator generator) {
            this.generator = generator;
            this.sink = ThreadedSource.this.output();
        }

        @Override
        public String toString() {
            return String.format("%s reading from %s", getClass().getName(), generator);
        }

        @Override
        public boolean executeIteration() throws IOException {
            try {
                if (!pool.isRunning())
                    return false;
                Sample sample = generator.nextSample();
                if (sample == null || !pool.isRunning())
                    return false;
                synchronized (outputLock) {
                    sink.writeSample(sample);
                }
                return true;
            } catch (IOException e) {
                boolean isFatal = fatalReaderExceptions();
                String fatalStr = isFatal ? "Fatal " : "Non-fatal ";
                logger.log(Level.SEVERE, fatalStr + " exception in " + toString(), e);
                if (isFatal)
                    close();
                return false;
            }
        }
    }

}

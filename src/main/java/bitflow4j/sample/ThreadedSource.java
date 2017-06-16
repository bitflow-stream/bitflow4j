package bitflow4j.sample;

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
 * are needed, for example when reading from TCP connections.
 * <p>
 * This does not implement StoppableSource, because it always stops on its own
 * and should not explicitly be stopped from the outside.
 * <p>
 * Created by anton on 23.12.16.
 */
public abstract class ThreadedSource extends AbstractSource implements ParallelTask {

    private static final Logger logger = Logger.getLogger(ThreadedSource.class.getName());

    protected final Object outputLock = new Object();
    private final List<LoopTask> tasks = new ArrayList<>();
    private boolean shuttingDown = false;

    public interface SampleGenerator {
        Sample nextSample() throws IOException;
    }

    public void readSamples(TaskPool pool, SampleGenerator generator) throws IOException {
        readSamples(pool, generator, false);
    }

    public void readSamples(TaskPool pool, SampleGenerator generator, boolean keepAlive) throws IOException {
        LoopTask task = new LoopSampleReader(generator);
        tasks.add(task);
        pool.start(task, keepAlive);
    }

    @Override
    public void run() throws IOException {
        // Wait for the shutdown and start the close() sequence with our direct output
        synchronized (this) {
            while (!shuttingDown)
                try {
                    wait();
                } catch (InterruptedException ignored) {
                }
        }
        tasks.forEach(LoopTask::waitForExit);
        output().close();
    }

    protected void shutDown() {
        synchronized (this) {
            shuttingDown = true;
            notifyAll();
        }
    }

    protected void stopTasks() {
        shutDown();
        tasks.forEach(LoopTask::stop);
    }

    protected boolean readerException() {
        // By default, do not shut down when an Exception occurs, keep going until the user shuts us down.
        return true;
    }

    protected void handleGeneratedSample(Sample sample) {
        // Do nothing. Hook for subclasses.
    }

    private class LoopSampleReader extends LoopTask {

        private final SampleGenerator generator;
        private final Sink sink;

        public LoopSampleReader(SampleGenerator generator) {
            this.generator = generator;
            this.sink = ThreadedSource.this.output();
        }

        @Override
        public String toString() {
            return generator.toString();
        }

        public boolean executeIteration() throws IOException {
            try {
                if (!pool.isRunning())
                    return false;
                Sample sample = generator.nextSample();
                if (sample == null || !pool.isRunning())
                    return false;
                handleGeneratedSample(sample);
                synchronized (outputLock) {
                    sink.writeSample(sample);
                }
                return true;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Exception in " + toString() +
                        ", running as part of: " + ThreadedSource.this, e);
                return readerException();
            }
        }

    }

}

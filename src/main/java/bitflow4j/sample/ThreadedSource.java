package bitflow4j.sample;

import bitflow4j.task.LoopTask;
import bitflow4j.task.ParallelTask;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.*;
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

    public void readSamples(TaskPool pool, List<SampleGenerator> generators, boolean keepAlive) throws IOException {
        LoopTask task = new LoopTimeSynchronizedMultiSampleReader(generators);
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

    // shutDown() should be closed in an overridden run() method, after starting all LoopSampleReaders.
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

        @Override
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
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Exception in " + toString() +
                        ", running as part of: " + ThreadedSource.this, e);
                return readerException();
            }
        }
    }

    private class LoopTimeSynchronizedMultiSampleReader extends LoopTask {

        private final List<SampleGenerator> generators;
        private List<SampleGenerator> currentGenerators;
        private Map<SampleGenerator, Queue<Sample>> queues = new HashMap<>();
        private final Sink sink;
        private boolean firstIteration = true;

        private final int synchronizationTolerance = 1000; //milliseconds

        public LoopTimeSynchronizedMultiSampleReader(List<SampleGenerator> generators) {
            this.generators = generators;
            this.currentGenerators = generators;
            for(SampleGenerator sg : generators)
                queues.put(sg, new LinkedList<>());
            this.sink = ThreadedSource.this.output();
        }

        @Override
        public String toString() {
            StringBuilder str = new StringBuilder();
            for(SampleGenerator sg : generators)
                str.append(sg.toString());
            return str.toString();
        }

        @Override
        public boolean executeIteration() throws IOException {
            if (!pool.isRunning())
                return false;
            ListIterator<SampleGenerator> iter = currentGenerators.listIterator();
            while(iter.hasNext()){
                SampleGenerator sg = iter.next();
                Sample sample;
                try {
                    sample = sg.nextSample();
                    if (!pool.isRunning())
                        return false;
                    if(sample == null){
                        iter.remove();
                        continue;
                    }
                } catch (IOException e) {
                    iter.remove();
                    continue;
                }
                try {
                    handleGeneratedSample(sample);
                    this.queues.get(sg).add(sample);
                    synchronized (outputLock) {
                        sink.writeSample(sample);
                    }
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Exception in " + toString() +
                            ", running as part of: " + ThreadedSource.this, e);
                    return readerException();
                }
            }
            if(currentGenerators.isEmpty()){
                System.out.println("Flushing rest samples");
                this.flushBufferedSamples();
            } else {
                if (!firstIteration)
                    this.updateCurrentGenerators();
                else
                    firstIteration = false;
            }
            return !currentGenerators.isEmpty();
        }

        private void updateCurrentGenerators() {
            Sample youngestSample = null;
            for(SampleGenerator sg : generators){
                Sample s = queues.get(sg).peek();
                if(s != null) {
                    if (youngestSample == null)
                        youngestSample = s;
                    if (s.getTimestamp().getTime() < youngestSample.getTimestamp().getTime())
                        youngestSample = s;
                }
            }

            List<SampleGenerator> relevantGenerators = new ArrayList<>();
            long refTime = youngestSample.getTimestamp().getTime();
            for(SampleGenerator sg : generators){
                Sample s = queues.get(sg).peek();
                if(s != null)
                    if(s.getTimestamp().getTime() <= (refTime + synchronizationTolerance))
                        relevantGenerators.add(sg);
            }

            for(SampleGenerator sg : currentGenerators)
                queues.get(sg).poll();

            currentGenerators = relevantGenerators;
        }

        private void flushBufferedSamples() throws IOException {
            Sample s;
            for(SampleGenerator sg : generators) {
                queues.get(sg).poll(); //Skip first sample since it was previously already sent
                while ((s = queues.get(sg).poll()) != null) {
                    this.sink.writeSample(s);
                }
            }
        }
    }
}

package bitflow4j.steps;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;
import bitflow4j.script.registry.BitflowConstructor;
import bitflow4j.script.registry.Optional;
import bitflow4j.task.StoppableLoopTask;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by anton on 17.02.17.
 * <p>
 * This pipeline step can be used to create parallelism in a pipeline.
 * Incoming samples are stored in a blocking queue, whose size can be configured.
 * When closing this pipeline step, the contents of the queue are flushed before propagating the close() call to the
 * outgoing sink.
 * The parameter-less constructor should usually not be used, as it will fill up the queue until the JVM runs
 * out of memory. The used queue size should be enough keep both the incoming and outgoing Threads busy.
 */
public class DecouplingPipelineStep extends AbstractPipelineStep {

    private final BlockingQueue<Sample> queue;
    private final StoppableLoopTask parallelWriter = new Writer();
    private final Sample closedMarker = Sample.newEmptySample();
    private boolean finishedFlushing = false;

    @BitflowConstructor
    public DecouplingPipelineStep(@Optional int queueSize) {
        if (queueSize <= 0) {
            this.queue = new LinkedBlockingDeque<>();
        } else {
            this.queue = new ArrayBlockingQueue<>(queueSize);
        }
    }

    public DecouplingPipelineStep() {
        this(0);
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        while (true) {
            try {
                queue.put(sample);
                break;
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public void start(TaskPool pool) throws IOException {
        super.start(pool);
        pool.start(parallelWriter);
    }

    @Override
    protected void doClose() throws IOException {
        parallelWriter.stop();
        synchronized (closedMarker) {
            while (!finishedFlushing)
                try {
                    closedMarker.wait();
                } catch (InterruptedException ignored) {
                }
        }
        super.doClose();
    }

    private class Writer extends StoppableLoopTask {

        @Override
        protected boolean executeIteration() throws IOException {
            Sample sample;
            while (true) {
                try {
                    sample = queue.take();
                    break;
                } catch (InterruptedException ignored) {
                }
            }
            if (sample == closedMarker) {
                synchronized (closedMarker) {
                    finishedFlushing = true;
                    super.stop();
                    closedMarker.notifyAll();
                }
                return false;
            }
            DecouplingPipelineStep.this.output().writeSample(sample);
            return true;
        }

        @Override
        public void stop() {
            queue.add(closedMarker);
        }

    }

}

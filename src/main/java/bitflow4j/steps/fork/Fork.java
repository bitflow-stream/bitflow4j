package bitflow4j.steps.fork;

import bitflow4j.*;
import bitflow4j.misc.Pair;
import bitflow4j.misc.TreeFormatter;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anton on 13.02.17.
 */
public class Fork extends AbstractPipelineStep implements TreeFormatter.FormattedNode {

    private static final Logger logger = Logger.getLogger(Fork.class.getName());

    private final Distributor distributor;
    private final Merger merger = new Merger();
    private final TaskPool pool = new TaskPool();

    private final Map<Pipeline, SubPipeline> subPipelines = new HashMap<>();

    public Fork(Distributor distributor) {
        this.distributor = distributor;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        Collection<Pair<String, Pipeline>> pipes = distributor.distribute(sample);
        if (pipes == null || pipes.isEmpty()) {
            // No sub pipelines selected. Directly forward the sample to our output step.
            super.writeSample(sample);
        } else if (pipes.size() == 1) {
            // One sub pipeline: no need to clone the sample, simply forward it.
            Pair<String, Pipeline> first = pipes.iterator().next();
            SubPipeline sinkInto = getPipeline(first.getLeft(), first.getRight());
            sinkInto.first.writeSample(sample);
        } else {
            // Multiple sub pipelines: clone the sample and forward it to each sub pipeline.
            // Cloning the sample is necessary because the sub-pipelines might
            // change the metrics independent of each other
            // TODO optimize all PipelineSteps to reuse the metrics array instead of copying
            // Make sure the samples are copied where necessary, like here.
            for (Pair<String, Pipeline> pipe : pipes) {
                SubPipeline sinkInto = getPipeline(pipe.getLeft(), pipe.getRight());
                double[] outMetrics = Arrays.copyOf(sample.getMetrics(), sample.getMetrics().length);
                Sample outSample = new Sample(sample.getHeader(), outMetrics, sample);
                sinkInto.first.writeSample(outSample);
            }
        }
    }

    @Override
    public void doClose() throws IOException {
        for (SubPipeline subPipeline : subPipelines.values()) {
            subPipeline.first.close();
        }
        for (SubPipeline subPipeline : subPipelines.values()) {
            subPipeline.last.waitUntilClosed();
        }
        pool.stop("Fork closed");
        pool.waitForTasks();
    }

    private SubPipeline getPipeline(String key, Pipeline pipe) throws IOException {
        if (subPipelines.containsKey(pipe)) {
            SubPipeline result = subPipelines.get(pipe);
            if (!key.equals(result.key)) {
                logger.info(String.format("SubPipeline '%s' reusing pipeline previously started for '%s'", key, result.key));
            }
            return result;
        } else {
            SubPipeline result = new SubPipeline(pipe, key);
            logger.fine(String.format("Starting forked sub pipeline for '%s'", key));
            if (pipe.source != null) {
                logger.log(Level.WARNING, String.format("The source field of the sub-pipeline for %s will be ignored, it is set to %s", key, pipe.source));
            }
            pipe.source = new EmptySource();
            pipe.step(merger);
            pipe.run(pool);
            subPipelines.put(pipe, result);
            return result;
        }
    }

    @Override
    public String toString() {
        if (distributor instanceof TreeFormatter.FormattedNode) {
            return distributor.toString();
        }
        return super.toString();
    }

    @Override
    public Collection<Object> formattedChildren() {
        if (distributor instanceof TreeFormatter.FormattedNode) {
            return ((TreeFormatter.FormattedNode) distributor).formattedChildren();
        }
        return Collections.singleton(distributor);
    }

    private static class SubPipeline {
        final Pipeline pipeline;
        final PipelineStep first;
        final PipelineStep last;
        final String key;

        private SubPipeline(Pipeline pipeline, String key) {
            this.key = key;
            this.pipeline = pipeline;
            this.first = pipeline.steps.get(0);
            this.last = pipeline.steps.get(pipeline.steps.size() - 1);
        }
    }

    private class Merger extends DroppingStep {

        private final Object lock = new Object();

        @Override
        public void writeSample(Sample sample) throws IOException {
            // Make sure to synchronize.
            synchronized (lock) {
                Fork.this.output().writeSample(sample);
            }
        }

    }

}

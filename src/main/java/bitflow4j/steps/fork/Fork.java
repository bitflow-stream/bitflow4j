package bitflow4j.steps.fork;

import bitflow4j.*;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anton on 13.02.17.
 */
public class Fork extends AbstractPipelineStep {

    private static final Logger logger = Logger.getLogger(Fork.class.getName());

    private final Distributor distributor;
    private final PipelineBuilder builder;
    private final Object sinkLock = new Object();
    private final TaskPool pool = new TaskPool();

    private final Map<Object, SubPipeline> subPipelines = new HashMap<>();

    public Fork(Distributor distributor, PipelineBuilder builder) {
        this.distributor = distributor;
        this.builder = builder;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        Object[] keys = distributor.distribute(sample);
        for (Object key : keys) {
            SubPipeline pipe = getPipeline(key);

            // Cloning the sample is necessary because the sub-pipelines might
            // change the metrics independent of each other
            // TODO optimize all PipelineSteps to reuse the metrics array instead of copying
            // Make sure the samples are copied where necessary, like here.
            double[] outMetrics = Arrays.copyOf(sample.getMetrics(), sample.getMetrics().length);
            Sample outSample = new Sample(sample.getHeader(), outMetrics, sample);
            pipe.first.writeSample(outSample);
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

    private SubPipeline getPipeline(Object key) throws IOException {
        if (subPipelines.containsKey(key)) {
            return subPipelines.get(key);
        }
        Pipeline pipeline = new Pipeline();
        SynchronizingSink merger = new SynchronizingSink(sinkLock, output());
        builder.build(key, pipeline, merger);
        if (pipeline.source != null) {
            logger.log(Level.WARNING,
                    "The source field of the sub-pipeline for {1} will be ignored, it is set to {2}", new Object[]{key, pipeline.source});
        }
        pipeline.source = new EmptySource();
        pipeline.step(merger);
        pipeline.run(pool);
        SubPipeline subPipeline = new SubPipeline(pipeline);
        subPipelines.put(key, subPipeline);
        return subPipeline;
    }

    private static class SubPipeline {
        final Pipeline pipeline;
        final PipelineStep first;
        final PipelineStep last;

        private SubPipeline(Pipeline pipeline) {
            this.pipeline = pipeline;
            this.first = pipeline.steps.get(0);
            this.last = pipeline.steps.get(pipeline.steps.size() - 1);
        }
    }

}

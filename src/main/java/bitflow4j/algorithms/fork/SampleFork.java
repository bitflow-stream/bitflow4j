package bitflow4j.algorithms.fork;

import bitflow4j.algorithms.AbstractAlgorithm;
import bitflow4j.main.AlgorithmPipeline;
import bitflow4j.sample.EmptySource;
import bitflow4j.sample.Sample;
import bitflow4j.sample.SampleSink;
import bitflow4j.sample.StoppableSampleSource;
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
public class SampleFork extends AbstractAlgorithm {

    private static final Logger logger = Logger.getLogger(SampleFork.class.getName());

    private final ForkDistributor distributor;
    private final PipelineBuilder builder;
    private SynchronizingSink merger;
    private TaskPool pool = new TaskPool();

    private final Map<Object, AlgorithmPipeline> subpipelines = new HashMap<>();

    public SampleFork(ForkDistributor distributor, PipelineBuilder builder) {
        this.distributor = distributor;
        this.builder = builder;
    }

    @Override
    public void start(TaskPool pool) throws IOException {
        merger = new SynchronizingSink(output());
        super.start(pool);
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        // TODO parallelism could be supported here
        Object keys[] = distributor.distribute(sample);
        for (Object key : keys) {
            AlgorithmPipeline pipe = getPipeline(key);
            SampleSink sink = pipe.steps.isEmpty() ? pipe.sink : pipe.steps.get(0);

            // Cloning the sample is necessary because the sub-pipelines might
            // change the metrics independent of each other
            // TODO optimize all Algorithms to reuse the metrics array instead of copying
            // Make sure the samples are copied where necessary, like here.
            double outMetrics[] = Arrays.copyOf(sample.getMetrics(), sample.getMetrics().length);
            Sample outSample = new Sample(sample.getHeader(), outMetrics, sample);
            sink.writeSample(outSample);
        }
    }

    @Override
    public void doClose() throws IOException {
        for (AlgorithmPipeline pipe : subpipelines.values()) {
            // The source is explicitly set to EmptySource below (which implements StoppableSampleSource)
            ((StoppableSampleSource) pipe.source).stop();
            pipe.sink.waitUntilClosed();
        }
        pool.stop("Fork closed");
        pool.waitForTasks();
    }

    private AlgorithmPipeline getPipeline(Object key) throws IOException {
        if (subpipelines.containsKey(key)) {
            return subpipelines.get(key);
        }
        AlgorithmPipeline pipeline = new AlgorithmPipeline();
        builder.build(key, pipeline, merger);
        if (pipeline.source != null) {
            logger.log(Level.WARNING,
                    "The source field of the subpipeline for {1} will be ignored, it is set to {2}", new Object[]{key, pipeline.source});
        }
        pipeline.source = new EmptySource();
        if (pipeline.sink == null) {
            pipeline.sink = merger;
        }

        pipeline.run(pool);
        subpipelines.put(key, pipeline);
        return pipeline;
    }

}

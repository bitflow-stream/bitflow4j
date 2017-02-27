package bitflow4j.algorithms.fork;

import bitflow4j.main.AlgorithmPipeline;

import java.io.IOException;

/**
 * Created by anton on 13.02.17.
 */
public interface PipelineBuilder {

    void build(Object key, AlgorithmPipeline subPipeline, SynchronizingSink output) throws IOException;

}

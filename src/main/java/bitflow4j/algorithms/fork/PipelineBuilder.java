package bitflow4j.algorithms.fork;

import bitflow4j.main.Pipeline;

import java.io.IOException;

/**
 * Created by anton on 13.02.17.
 */
public interface PipelineBuilder {

    void build(Object key, Pipeline subPipeline, SynchronizingSink output) throws IOException;

}

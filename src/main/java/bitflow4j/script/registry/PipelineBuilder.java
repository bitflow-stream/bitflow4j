package bitflow4j.script.registry;

import bitflow4j.Pipeline;

import java.io.IOException;

/**
 * PipelineBuilder defines a method to extend a pipeline from provided parameters.
 */
public interface PipelineBuilder {

    void buildPipeline(Pipeline pipeline) throws IOException;

}

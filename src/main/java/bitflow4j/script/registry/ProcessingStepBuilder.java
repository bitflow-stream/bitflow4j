package bitflow4j.script.registry;

import bitflow4j.PipelineStep;

import java.io.IOException;
import java.util.Map;

/**
 * ProcessingStepBuilder defines a method to extend a pipeline from provided parameters.
 * The type parameter can be one of: PipelineStep, BatchHandler or ScriptableDistributor.
 */
public interface ProcessingStepBuilder {

    PipelineStep buildProcessingStep(Map<String, Object> parameters) throws IOException;

}

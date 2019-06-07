package bitflow4j.script.registry;

import bitflow4j.steps.BatchHandler;

import java.io.IOException;
import java.util.Map;

public interface BatchStepBuilder {

    BatchHandler buildBatchStep(Map<String, Object> parameters) throws IOException;

}

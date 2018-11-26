package bitflow4j;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * A PipelineStep receives metrics like a PipelineStep and forwards results
 * to another PipelineStep.
 * The way it does this is completely open: many (or even all) samples can be received
 * before outputting anything, or every sample can be modified and forwarded independently.
 */
public interface PipelineStep extends Source {

    Logger logger = Logger.getLogger(PipelineStep.class.getName());

    void writeSample(Sample sample) throws IOException;

    void close();

    void waitUntilClosed();

}

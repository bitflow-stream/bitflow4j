package bitflow4j;

import bitflow4j.task.Task;

/**
 * Basic interface for reading a stream of Samples.
 *
 * Created by anton on 30.03.16.
 */
public interface Source extends Task {

    void setOutgoingSink(PipelineStep sink);

    void close();

}

package bitflow4j.sample;

import bitflow4j.task.Task;

/**
 * Basic interface for reading a stream of Samples.
 * <p>
 * Created by mwall on 30.03.16.
 */
public interface Source extends Task {

    void setOutgoingSink(Sink sink);

}

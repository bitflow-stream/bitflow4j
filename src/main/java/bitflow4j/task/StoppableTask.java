package bitflow4j.task;

import java.io.IOException;

/**
 * Created by anton on 14.02.17.
 */
public interface StoppableTask extends Task {

    void stop() throws IOException;

}

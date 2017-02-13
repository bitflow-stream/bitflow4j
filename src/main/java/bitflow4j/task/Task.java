package bitflow4j.task;

import java.io.IOException;

/**
 * Created by anton on 13.02.17.
 */
public interface Task {

    void start(TaskPool pool) throws IOException;

}

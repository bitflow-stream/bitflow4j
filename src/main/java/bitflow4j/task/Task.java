package bitflow4j.task;

import java.io.IOException;

/**
 * Created by anton on 13.02.17.
 */
public interface Task {

    // This method should only be called by a TaskPool.
    // A Task can be started through taskPool.start(task).
    void start(TaskPool pool) throws IOException;

}

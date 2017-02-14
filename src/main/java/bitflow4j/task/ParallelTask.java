package bitflow4j.task;

import java.io.IOException;

/**
 * Created by anton on 13.02.17.
 * <p>
 * After preparations done in the start() method, a ParallelTask will be executed in a separate
 * Thread by running its run() method.
 * A stop() method is not defined, but implementations should provide a stop() method that allows
 * users to make the run() method (and with it the executing thread) exit.
 * By explicitly implementing the StoppableTask interface, the TaskPool will automatically call the
 * stop() method.
 */
public interface ParallelTask extends Task {

    void run() throws IOException;

}

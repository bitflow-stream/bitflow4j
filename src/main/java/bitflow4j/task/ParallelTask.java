package bitflow4j.task;

/**
 * Created by anton on 13.02.17.
 * <p>
 * Marker interface that tells the TaskPool that this Task should be started in a separate Thread.
 * No additional methods are defined, but implementations should provide a stop() method that allows
 * users to make the start() method (and with it the executing thread) exit.
 */
public interface ParallelTask extends Task {
}

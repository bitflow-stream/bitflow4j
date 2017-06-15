package bitflow4j.sample;

import bitflow4j.task.TaskPool;

import java.io.IOException;

/**
 * Created by anton on 13.02.17.
 */
public class EmptySource extends AbstractStoppableSource {

    @Override
    public void start(TaskPool pool) throws IOException {
        // Do nothing
    }

}

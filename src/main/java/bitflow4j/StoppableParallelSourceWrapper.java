package bitflow4j;

import bitflow4j.task.ParallelTask;

import java.io.IOException;

public class StoppableParallelSourceWrapper extends StoppableSourceWrapper implements ParallelTask {

    private final ParallelTask wrapped;

    public StoppableParallelSourceWrapper(Source source) {
        super(source);
        if (!(source instanceof ParallelTask)) {
            throw new IllegalArgumentException("Wrapped source must implement ParallelTask");
        }
        wrapped = (ParallelTask) source;
    }

    @Override
    public void run() throws IOException {
        wrapped.run();
    }

}

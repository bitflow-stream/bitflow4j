package bitflow4j.sample;

import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by anton on 15.06.17.
 */
public class ListSource extends ThreadedSource implements ThreadedSource.SampleGenerator {

    private TaskPool pool;
    private final List<Sample> samples;
    private Iterator<Sample> iterator = null;

    // Configuration variables
    public long pauseBetweenSamples = 0;
    public boolean endlessLoop = false;

    public ListSource(List<Sample> samples) {
        this.samples = samples;
    }

    @Override
    public void start(TaskPool pool) throws IOException {
        this.pool = pool;
        readSamples(pool, this);
    }

    @Override
    public synchronized Sample nextSample() throws IOException {
        if (pauseBetweenSamples > 0) {
            pool.sleep(pauseBetweenSamples);
        }
        if (iterator == null || (!iterator.hasNext() && endlessLoop)) {
            iterator = samples.iterator();
        }
        return iterator.hasNext() ? iterator.next() : null;
    }

}

package bitflow4j.io.list;

import bitflow4j.Sample;
import bitflow4j.io.ThreadedSource;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by anton on 15.06.17.
 */
public class ListSource extends ThreadedSource {

    // Configuration variables
    public long pauseBetweenSamples = 0;
    public boolean endlessLoop = false;

    private final List<Sample> samples;

    public ListSource(List<Sample> samples) {
        this.samples = samples;
    }

    @Override
    public void start(TaskPool pool) throws IOException {
        super.start(pool);
        readSamples(new ListSampleGenerator());
        initFinished();
    }

    @Override
    public String toString() {
        String loopInfo = endlessLoop ? ", endless loop" : "";
        String pauseInfo = pauseBetweenSamples > 0 ? ", pauses of " + pauseBetweenSamples + "ms" : "";
        return String.format("ListSource (%s samples%s%s)", samples.size(), loopInfo, pauseInfo);
    }

    public class ListSampleGenerator implements ThreadedSource.SampleGenerator {

        private Iterator<Sample> iterator = null;

        @Override
        public String toString() {
            return "Sample generator for " + ListSource.this;
        }

        @Override
        public synchronized Sample nextSample() {
            if (pauseBetweenSamples > 0) {
                if (!pool.sleep(pauseBetweenSamples))
                    return null;
            }
            if (iterator == null || (!iterator.hasNext() && endlessLoop)) {
                iterator = samples.iterator();
            }
            return iterator.hasNext() ? iterator.next() : null;
        }

    }

}

package bitflow4j.sample;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by anton on 15.06.17.
 */
public class ListSink extends AbstractSink {

    public ListSink() {
        this(new LinkedList<>());
    }

    public ListSink(List<Sample> samples) {
        this.samples = samples;
    }

    public final List<Sample> samples;

    @Override
    public void writeSample(Sample sample) throws IOException {
        samples.add(sample);
    }

}

package bitflow4j.io.list;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by anton on 15.06.17.
 */
public class ListSink extends AbstractPipelineStep {

    public ListSink() {
        this(new LinkedList<>());
    }

    public ListSink(List<Sample> samples) {
        this.samples = samples;
    }

    public final List<Sample> samples;

    @Override
    public String toString() {
        return "Sink storing samples in list";
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        samples.add(sample);
        super.writeSample(sample);
    }

}

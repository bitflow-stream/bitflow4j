package metrics.algorithms;

import metrics.Header;
import metrics.Sample;

import java.io.IOException;

/**
 * Created by anton on 4/14/16.
 * <p>
 * Assigns new labels to incoming Samples.
 */
public abstract class LabellingAlgorithm extends AbstractAlgorithm {

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        String label = newLabel(sample);
        Header header = new Header(sample.getHeader().header);
        Sample outSample = new Sample(header, sample.getMetrics(), sample);
        outSample.setLabel(label);
        return outSample;
    }

    protected abstract String newLabel(Sample sample);

    @Override
    public String toString() {
        return "labelling algorithm";
    }

}

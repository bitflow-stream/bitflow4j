package metrics.main.analysis;

import metrics.Header;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;

import java.io.IOException;

/**
 * Created by anton on 6/9/16.
 */
public class SampleClearer extends AbstractAlgorithm {

    // Remove the sample data, only keep timestamp and tags
    protected Sample executeSample(Sample sample) throws IOException {
        return new Sample(Header.EMPTY_HEADER, new double[0], sample.getTimestamp(), sample.getTags());
    }

    @Override
    public String toString() {
        return "sample clearer";
    }
}

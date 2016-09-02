package metrics.algorithms.filter;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.main.misc.ParameterHash;

import java.io.IOException;

/**
 * Samples can be filtered out based on some user defined rule.
 *
 * Created by anton on 4/23/16.
 */
public class SampleFilterAlgorithm extends AbstractAlgorithm {

    public interface Filter {
        boolean shouldInclude(Sample sample);
    }

    private final Filter filter;

    public SampleFilterAlgorithm(Filter filter) {
        this.filter = filter;
    }

    protected Sample executeSample(Sample sample) throws IOException {
        if (filter.shouldInclude(sample)) {
            return sample;
        } else {
            return null;
        }
    }

    @Override
    public void hashParameters(ParameterHash hash) {
        super.hashParameters(hash);
        hash.writeClassName(filter);
    }

    @Override
    public String toString() {
        return "sample filter";
    }

}

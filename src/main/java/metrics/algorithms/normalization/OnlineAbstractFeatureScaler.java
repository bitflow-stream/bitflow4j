package metrics.algorithms.normalization;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by anton on 6/23/16.
 */
public abstract class OnlineAbstractFeatureScaler extends AbstractAlgorithm {

    private Set<String> warnedMetrics = new HashSet<String>();

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        double[] inMetrics = sample.getMetrics();
        String[] fields = sample.getHeader().header;
        double[] metrics = new double[inMetrics.length];
        for (int i = 0; i < inMetrics.length; i++) {
            metrics[i] = doStandardize(fields[i], inMetrics[i]);
        }
        return new Sample(sample.getHeader(), metrics, sample);
    }

    private double doStandardize(String name, double val) {
        if (!canStandardize(name)) {
            if (!warnedMetrics.contains(name)) {
                warnedMetrics.add(name);
                System.err.println("WARNING: Missing stddev/average information for metric " + name + ", not standardizing!");
            }
            return val;
        }
        return standardize(name, val);
    }

    protected abstract boolean canStandardize(String name);

    protected abstract double standardize(String name, double val);

}

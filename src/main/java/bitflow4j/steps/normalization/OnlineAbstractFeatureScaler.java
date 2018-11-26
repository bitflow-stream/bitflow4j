package bitflow4j.steps.normalization;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by anton on 6/23/16.
 */
public abstract class OnlineAbstractFeatureScaler extends AbstractPipelineStep {

    private static final Logger logger = Logger.getLogger(OnlineAbstractFeatureScaler.class.getName());

    private Set<String> warnedMetrics = new HashSet<>();

    @Override
    public void writeSample(Sample sample) throws IOException {
        double[] inMetrics = sample.getMetrics();
        String[] fields = sample.getHeader().header;
        double[] metrics = new double[inMetrics.length];
        for (int i = 0; i < inMetrics.length; i++) {
            metrics[i] = doStandardize(fields[i], inMetrics[i]);
        }
        output.writeSample(new Sample(sample.getHeader(), metrics, sample));
    }

    private double doStandardize(String name, double val) {
        if (!canStandardize(name)) {
            if (!warnedMetrics.contains(name)) {
                warnedMetrics.add(name);
                logger.warning("WARNING: Missing stddev/average information for metric " + name + ", not standardizing!");
            }
            return val;
        }
        return standardize(name, val);
    }

    protected abstract boolean canStandardize(String name);

    protected abstract double standardize(String name, double val);

}

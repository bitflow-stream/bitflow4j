package metrics.algorithms;

import metrics.Sample;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by anton on 6/9/16.
 */
public class OnlineFeatureStandardizer extends AbstractAlgorithm {

    private final Map<String, Double> averages;
    private final Map<String, Double> stddevs;

    private Set<String> warnedMetrics = new HashSet<String>();

    public OnlineFeatureStandardizer(Map<String, Double> averages, Map<String, Double> stddevs) {
        this.averages = averages;
        this.stddevs = stddevs;
    }

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        double[] inMetrics = sample.getMetrics();
        String[] fields = sample.getHeader().header;
        double[] metrics = new double[inMetrics.length];
        for (int i = 0; i < inMetrics.length; i++) {
            metrics[i] = standardize(fields[i], inMetrics[i]);
        }
        return new Sample(sample.getHeader(), metrics, sample.getTimestamp(), sample.getTags());
    }

    private double standardize(String name, double val) {
        if (!averages.containsKey(name) || !stddevs.containsKey(name)) {
            if (!warnedMetrics.contains(name)) {
                warnedMetrics.add(name);
                System.err.println("WARNING: Missing stddev/average information for metric " + name + ", not standardizing!");
            }
            return val;
        }
        double average = averages.get(name);
        double stdDeviation = stddevs.get(name);
        if (stdDeviation == 0) stdDeviation = 1;
        return (val - average) / stdDeviation;
    }

    @Override
    public String toString() {
        return "online feature standardizer";
    }

}

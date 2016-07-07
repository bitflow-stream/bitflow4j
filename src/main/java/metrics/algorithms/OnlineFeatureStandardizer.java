package metrics.algorithms;

import java.util.Map;

/**
 * Created by anton on 6/9/16.
 */
public class OnlineFeatureStandardizer extends OnlineAbstractFeatureScaler {

    private final Map<String, Double> averages;
    private final Map<String, Double> stddevs;

    public OnlineFeatureStandardizer(Map<String, Double> averages, Map<String, Double> stddevs) {
        this.averages = averages;
        this.stddevs = stddevs;
    }

    protected boolean canStandardize(String name) {
        return averages.containsKey(name) && stddevs.containsKey(name);
    }

    protected double standardize(String name, double val) {
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

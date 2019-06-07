package bitflow4j.steps.normalization;

import bitflow4j.misc.FeatureStatistics;
import bitflow4j.misc.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anton on 6/9/16.
 */
public class OnlineStandardizer extends AbstractOnlineScaler {

    private final Map<String, Double> averages;
    private final Map<String, Double> stddevs;

    public OnlineStandardizer(Map<String, Double> averages, Map<String, Double> stddevs) {
        this.averages = averages;
        this.stddevs = stddevs;
    }

    public OnlineStandardizer(FeatureStatistics stats) {
        this(new HashMap<>(), new HashMap<>());
        for (FeatureStatistics.Feature ft : stats.allFeatures()) {
            averages.put(ft.getName(), ft.getAvg());
            stddevs.put(ft.getName(), ft.getStddev());
        }
    }

    protected boolean canScale(String name) {
        return averages.containsKey(name) && stddevs.containsKey(name);
    }

    protected Pair<Double, Boolean> scale(String name, double val) {
        double average = averages.get(name);
        double stdDeviation = stddevs.get(name);
        if (stdDeviation == 0) stdDeviation = 1;
        double scaledValue = (val - average) / stdDeviation;
        return new Pair<>(scaledValue, false);
    }

    @Override
    protected void updateScaling(String name) {
        // The scaling model is static and not updated
    }

    @Override
    public String toString() {
        return "Static online standardizer";
    }

}

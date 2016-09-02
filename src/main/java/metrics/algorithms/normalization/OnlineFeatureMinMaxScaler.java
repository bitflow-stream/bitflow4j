package metrics.algorithms.normalization;

import java.util.Map;

/**
 * Created by anton on 6/23/16.
 */
public class OnlineFeatureMinMaxScaler extends OnlineAbstractFeatureScaler {

    private final Map<String, Double> mins;
    private final Map<String, Double> maxs;

    public OnlineFeatureMinMaxScaler(Map<String, Double> mins, Map<String, Double> maxs) {
        this.mins = mins;
        this.maxs = maxs;
    }

    protected boolean canStandardize(String name) {
        return mins.containsKey(name) && maxs.containsKey(name);
    }

    protected double standardize(String name, double val) {
        double min = mins.get(name);
        double max = maxs.get(name);
        double range = max - min;
        if (range == 0)
            return val;
        return (val - min) / range;
    }

    @Override
    public String toString() {
        return "online feature min-max scaler";
    }

}

package bitflow4j.steps.normalization;

import bitflow4j.misc.OnlineStatistics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anton on 7/7/16.
 */
public class OnlineAutoFeatureStandardizer extends OnlineAbstractFeatureScaler {

    private final Map<String, OnlineStatistics> features = new HashMap<>();

    @Override
    protected boolean canStandardize(String name) {
        return true;
    }

    protected double standardize(String name, double val) {
        OnlineStatistics estimator = features.get(name);
        if (estimator == null) {
            estimator = new OnlineStatistics();
            features.put(name, estimator);
        }
        estimator.push(val);
        double average = estimator.mean();
        double stdDeviation = estimator.standardDeviation();
        if (stdDeviation == 0) stdDeviation = 1;
        return (val - average) / stdDeviation;
    }

}

package metrics.algorithms.normalization;

import metrics.algorithms.OnlineNormalEstimator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anton on 7/7/16.
 */
public class OnlineAutoFeatureStandardizer extends OnlineAbstractFeatureScaler {

    private final Map<String, OnlineNormalEstimator> features = new HashMap<>();

    @Override
    protected boolean canStandardize(String name) {
        return true;
    }

    protected double standardize(String name, double val) {
        OnlineNormalEstimator estimator = features.get(name);
        if (estimator == null) {
            estimator = new OnlineNormalEstimator();
            features.put(name, estimator);
        }
        estimator.handle(val);
        double average = estimator.mean();
        double stdDeviation = estimator.standardDeviation();
        if (stdDeviation == 0) stdDeviation = 1;
        return (val - average) / stdDeviation;
    }

}

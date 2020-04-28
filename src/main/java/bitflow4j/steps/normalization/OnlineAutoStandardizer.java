package bitflow4j.steps.normalization;

import bitflow4j.misc.OnlineStatistics;
import bitflow4j.steps.misc.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anton on 7/7/16.
 */
public class OnlineAutoStandardizer extends AbstractOnlineScaler {

    private final Map<String, OnlineStatistics> features = new HashMap<>();

    @Override
    protected boolean canScale(String name) {
        return true;
    }

    @Override
    protected Pair<Double, Boolean> scale(String name, double val, ConceptChangeDetector detector) {
        OnlineStatistics estimator = features.get(name);
        if (estimator == null) {
            estimator = new OnlineStatistics();
            features.put(name, estimator);
        }
        estimator.push(val);
        double average = estimator.mean();
        double stdDeviation = estimator.standardDeviation();
        if (stdDeviation == 0) stdDeviation = 1;
        double scaledValue = (val - average) / stdDeviation;

        // TODO implement handling of changed scaling model (see OnlineAutoMinMaxScaler)
        return new Pair<>(scaledValue, detector.isConceptChanged(scaledValue));
    }

    @Override
    protected void updateScaling(String name) {
        // Updates are always applied automatically
    }

}

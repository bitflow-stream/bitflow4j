package bitflow4j.steps.normalization;

import bitflow4j.steps.misc.Pair;

import java.util.Map;

/**
 * Created by anton on 6/23/16.
 */
public class OnlineMinMaxScaler extends AbstractOnlineScaler {

    private final Map<String, Double> mins;
    private final Map<String, Double> maxs;

    public OnlineMinMaxScaler(Map<String, Double> mins, Map<String, Double> maxs) {
        this.mins = mins;
        this.maxs = maxs;
    }

    @Override
    protected boolean canScale(String name) {
        return mins.containsKey(name) && maxs.containsKey(name);
    }

    @Override
    protected Pair<Double, Boolean> scale(String name, double val, ConceptChangeDetector detector) {
        double min = mins.get(name);
        double max = maxs.get(name);
        double range = max - min;
        double scaledValue = val;
        if (range != 0)
            scaledValue = (val - min) / range;
        return new Pair<>(scaledValue, detector.isConceptChanged(scaledValue));
    }

    @Override
    protected void updateScaling(String name) {
        // The scaling model is static
    }

    @Override
    public String toString() {
        return "Static online feature min-max scaler";
    }

}

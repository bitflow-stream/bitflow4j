package metrics.algorithms;

import metrics.main.prototype.FeatureStatistics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anton on 7/7/16.
 */
public class OnlineAutoMinMaxScaler extends OnlineAbstractFeatureScaler {

    public interface ConceptChangeHandler {
        void conceptChanged(OnlineAutoMinMaxScaler scaler, String feature);
    }

    public final Map<String, Feature> features = new HashMap<>();

    // In percent: how much must min/max values change before a concept change is fired
    private final double conceptChangeThreshold;

    private final ConceptChangeHandler handler;

    public OnlineAutoMinMaxScaler(double conceptChangeThreshold, ConceptChangeHandler handler, FeatureStatistics stats) {
        this.conceptChangeThreshold = conceptChangeThreshold;
        this.handler = handler;
        if (stats != null) {
            for (FeatureStatistics.Feature stat : stats.allFeatures()) {
                features.put(stat.name, new Feature(stat.name, stat.min, stat.max, conceptChangeThreshold));
            }
        }
    }

    // Will silently adjust the scalings
    public OnlineAutoMinMaxScaler() {
        this(0, null, null);
    }

    // Will start with given values, then silently adjust the scalings
    public OnlineAutoMinMaxScaler(FeatureStatistics stats) {
        this(0, null, stats);
    }

    // Start with nothing and notify changed scalings
    public OnlineAutoMinMaxScaler(double conceptChangeThreshold, ConceptChangeHandler handler) {
        this(conceptChangeThreshold, handler, null);
    }

    @Override
    protected boolean canStandardize(String name) {
        return true;
    }

    @Override
    protected double standardize(String name, double val) {
        Feature stat = features.get(name);
        if (stat == null) {
            stat = new Feature(name, val, val, conceptChangeThreshold);
            features.put(name, stat);
        }
        if (stat.push(val)) {
            if (handler != null)
                handler.conceptChanged(this, name);
        }
        return stat.standardize(val);
    }

    public static class Feature {
        Feature(String name, double min, double max, double threshold) {
            this.min = this.newMin = min;
            this.max = this.newMax = max;
            this.name = name;
            this.threshold = threshold;
        }

        final double threshold;
        final String name;

        public double min;
        public double max;

        double newMin;
        double newMax;

        double range() {
            double range = Math.abs(max - min);
            if (range == 0) range = max; // TODO not really correct
            return range;
        }

        // return true upon concept change
        boolean push(double val) {
            if (val < newMin) newMin = val;
            if (val > newMax) newMax = val;
            if (threshold <= 0) {
                // Fast path: immediately adjust min/max for all future scalings
                boolean changed = min > newMin || max < newMax;
                min = newMin;
                max = newMax;
                return changed;
            }
            double range = range();
            if (range == 0) range = 1;
            double diffMin = Math.abs(min - newMin);
            double diffMax = Math.abs(max - newMax);
            if (diffMin / range > threshold || diffMax / range > threshold) {
                min = newMin;
                max = newMax;
                return true;
            }
            return false;
        }

        double standardize(double val) {
            double range = range();
            if (range == 0) {
                // There is no correct value here, but return something in the 0-1 range.
                return 0;
            }
            return (val - min) / range;
        }
    }

    @Override
    public String toString() {
        return "online auto min-max scaler";
    }

}

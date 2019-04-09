package bitflow4j.steps.normalization;

import bitflow4j.misc.FeatureStatistics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anton on 7/7/16.
 */
public class OnlineAutoMinMaxScaler extends OnlineAbstractFeatureScaler {

    public interface ConceptChangeHandler {
        // Return false -> ignore this concept-change
        boolean conceptChanged(OnlineAutoMinMaxScaler scaler, String feature);
    }

    public static ConceptChangeHandler noConceptChanged = (s, f) -> false;

    public final Map<String, Feature> features;

    // In percent: how much must scalingMin/scalingMax values change before a concept change is fired
    private final double conceptChangeThreshold;

    private ConceptChangeHandler handler;

    public OnlineAutoMinMaxScaler(double conceptChangeThreshold, ConceptChangeHandler handler, FeatureStatistics stats) {
        this.conceptChangeThreshold = conceptChangeThreshold;
        this.handler = handler;
        this.features = new HashMap<>();
        setFeatures(stats);
    }

    public OnlineAutoMinMaxScaler(ConceptChangeHandler handler, Map<String, Feature> features, double conceptChangeThreshold) {
        this.conceptChangeThreshold = conceptChangeThreshold;
        this.handler = handler;
        this.features = features;
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
            if (handler == null || handler.conceptChanged(this, name)) {
                stat.flushScalingValues();
            }
        }
        return stat.standardize(val);
    }

    public static class Feature {
        Feature(String name, double min, double max, double threshold) {
            this.scalingMin = this.reportedMin = this.observedMin = min;
            this.scalingMax = this.reportedMax = this.observedMax = max;
            this.name = name;
            this.threshold = threshold;
        }

        final double threshold;
        final String name;

        // Used for actual scaling
        public double scalingMin;
        public double scalingMax;

        // Have been reported as concept change
        // Returning false from ConceptChangeHandler.conceptChanged() prevents this from being used as scalingMin/scalingMax
        public double reportedMin;
        public double reportedMax;

        // The actual reportedMin/reportedMax values from all the observed values so far
        private double observedMin;
        private double observedMax;

        // return true upon concept change
        boolean push(double val) {
            if (val < observedMin) observedMin = val;
            if (val > observedMax) observedMax = val;
            if (threshold <= 0) {
                // Fast path: immediately adjust scalingMin/scalingMax for all future scalings
                boolean changed = reportedMin > observedMin || reportedMax < observedMax;
                reportedMin = observedMin;
                reportedMax = observedMax;
                return changed;
            }

            double range = Math.abs(reportedMax - reportedMin);
            if (range == 0) range = reportedMax; // TODO not really correct
            if (range == 0) range = 1;

            double diffMin = Math.abs(reportedMin - observedMin);
            double diffMax = Math.abs(reportedMax - observedMax);
            if (diffMin / range > threshold || diffMax / range > threshold) {
                reportedMin = observedMin;
                reportedMax = observedMax;
                return true;
            }
            return false;
        }

        void flushScalingValues() {
            scalingMin = reportedMin;
            scalingMax = reportedMax;
        }

        double standardize(double val) {
            double range = Math.abs(scalingMax - scalingMin);
            if (range == 0) range = scalingMax; // TODO not really correct
            if (range == 0) return 0; // There is no correct value here, but return something in the 0-1 range.
            return (val - scalingMin) / range;
        }
    }

    public Map<String, Feature> getFeatures() {
        return features;
    }

    public void setFeatures(FeatureStatistics stats) {
        if (stats != null) {
            for (FeatureStatistics.Feature stat : stats.allFeatures()) {
                features.put(stat.name, new Feature(stat.name, stat.min, stat.max, conceptChangeThreshold));
            }
        }
    }

    public void setConceptChangeHandler(ConceptChangeHandler handler) {
        this.handler = handler;
    }

    @Override
    public String toString() {
        return "online auto min-max scaler";
    }

}

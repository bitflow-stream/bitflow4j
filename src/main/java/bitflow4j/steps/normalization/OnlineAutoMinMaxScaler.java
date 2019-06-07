package bitflow4j.steps.normalization;

import bitflow4j.misc.FeatureStatistics;
import bitflow4j.misc.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anton on 7/7/16.
 */
public class OnlineAutoMinMaxScaler extends AbstractOnlineScaler {

    public final Map<String, Feature> features;

    // In percent: how much must scalingMin/scalingMax values change before a concept change is fired
    private final double conceptChangeThreshold;

    public OnlineAutoMinMaxScaler() {
        this(0);
    }

    // By default, all changed concepts are accepted
    public OnlineAutoMinMaxScaler(double conceptChangeThreshold) {
        this(conceptChangeThreshold, acceptAllChangedConcepts(true));
    }

    public OnlineAutoMinMaxScaler(double conceptChangeThreshold, ConceptChangeHandler handler) {
        super(handler);
        this.conceptChangeThreshold = conceptChangeThreshold;
        this.features = new HashMap<>();
    }

    public OnlineAutoMinMaxScaler(double conceptChangeThreshold, ConceptChangeHandler handler, Map<String, Feature> features) {
        super(handler);
        this.conceptChangeThreshold = conceptChangeThreshold;
        this.features = features;
    }

    @Override
    protected boolean canScale(String name) {
        return true;
    }

    @Override
    protected Pair<Double, Boolean> scale(String name, double val) {
        Feature stat = features.get(name);
        if (stat == null) {
            stat = new Feature(name, val, val, conceptChangeThreshold);
            features.put(name, stat);
        }
        boolean conceptChanged = stat.push(val);
        double scaledValue = stat.standardize(val);
        return new Pair<>(scaledValue, conceptChanged);
    }

    @Override
    protected void updateScaling(String name) {
        features.get(name).flushScalingValues();
    }

    public static class Feature {
        Feature(String name, double min, double max, double threshold) {
            this.scalingMin = min;
            this.reportedMin = min;
            this.observedMin = min;
            this.scalingMax = max;
            this.reportedMax = max;
            this.observedMax = max;
            this.name = name;
            this.threshold = threshold;
        }

        final double threshold;
        final String name;

        // Used for actual scaling
        private double scalingMin;
        private double scalingMax;

        // Have been reported as concept change
        // Returning false from ConceptChangeHandler.conceptChanged() prevents this from being used as scalingMin/scalingMax
        private double reportedMin;
        private double reportedMax;

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

        public String getName() {
            return name;
        }

        public double getScalingMin() {
            return scalingMin;
        }

        public double getScalingMax() {
            return scalingMax;
        }

        public double getReportedMin() {
            return reportedMin;
        }

        public double getReportedMax() {
            return reportedMax;
        }

        public double getObservedMin() {
            return observedMin;
        }

        public double getObservedMax() {
            return observedMax;
        }
    }

    public Map<String, Feature> getFeatures() {
        return features;
    }

    public void setFeatures(FeatureStatistics stats) {
        for (FeatureStatistics.Feature stat : stats.allFeatures()) {
            features.put(stat.name, new Feature(stat.name, stat.min, stat.max, conceptChangeThreshold));
        }
    }

    @Override
    public String toString() {
        return "Online auto min-max scaler";
    }

}

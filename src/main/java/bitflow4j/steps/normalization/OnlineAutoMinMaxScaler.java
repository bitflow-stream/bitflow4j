package bitflow4j.steps.normalization;

import bitflow4j.misc.FeatureStatistics;
import bitflow4j.misc.Pair;
import bitflow4j.script.registry.BitflowConstructor;
import bitflow4j.script.registry.Optional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anton on 7/7/16.
 */
public class OnlineAutoMinMaxScaler extends AbstractOnlineScaler {

    public final Map<String, Feature> features;

    public OnlineAutoMinMaxScaler() {
        this(0.1);
    }

    @BitflowConstructor
    public OnlineAutoMinMaxScaler(@Optional(defaultDouble = 0.1) double conceptChangeThreshold) {
        this(conceptChangeThreshold, acceptAllChangedConcepts(true));
    }

    public OnlineAutoMinMaxScaler(ConceptChangeDetector detector) {
        this(detector, acceptAllChangedConcepts(true), new HashMap<>());
    }

    public OnlineAutoMinMaxScaler(double conceptChangeThreshold, ConceptChangeHandler handler) {
        this(conceptChangeThreshold, handler, new HashMap<>());
    }

    public OnlineAutoMinMaxScaler(ConceptChangeDetector detector, ConceptChangeHandler handler) {
        this(detector, handler, new HashMap<>());
    }

    public OnlineAutoMinMaxScaler(double conceptChangeThreshold, ConceptChangeHandler handler, Map<String, Feature> features) {
        this(new ThresholdConceptChangeDetector(conceptChangeThreshold), handler, features);
    }

    public OnlineAutoMinMaxScaler(ConceptChangeDetector detector, ConceptChangeHandler handler, Map<String, Feature> features) {
        super(handler, detector);
        this.features = features;
    }

    @Override
    protected boolean canScale(String name) {
        return true;
    }

    @Override
    protected Pair<Double, Boolean> scale(String name, double val, ConceptChangeDetector detector) {
        Feature stat = features.get(name);
        if (stat == null) {
            stat = new Feature(name, val, val, detector);
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
        Feature(String name, double min, double max, ConceptChangeDetector detector) {
            this.scalingMin = min;
            this.reportedMin = min;
            this.observedMin = min;
            this.scalingMax = max;
            this.reportedMax = max;
            this.observedMax = max;
            this.name = name;
            this.detector = detector;
        }

        final ConceptChangeDetector detector;
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

            double range = Math.abs(reportedMax - reportedMin);
            if (range == 0) range = reportedMax; // TODO not really correct
            if (range == 0) range = 1;

            double diffMin = Math.abs(reportedMin - observedMin);
            double diffMax = Math.abs(reportedMax - observedMax);

            if (detector.isConceptChanged(diffMin / range) || detector.isConceptChanged(diffMax / range)) {
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

        public ConceptChangeDetector getDetector() {
            return detector;
        }
    }

    public Map<String, Feature> getFeatures() {
        return features;
    }

    public void setFeatures(FeatureStatistics stats) {
        for (FeatureStatistics.Feature stat : stats.allFeatures()) {
            features.put(stat.getName(), new Feature(stat.getName(), stat.getMin(), stat.getMax(), super.getDetector()));
        }
    }

    @Override
    public String toString() {
        return "Online auto min-max scaler";
    }

    protected static class ThresholdConceptChangeDetector implements ConceptChangeDetector{
        private final double threshold;

        public ThresholdConceptChangeDetector(double threshold) {
            this.threshold = threshold;
        }

        @Override
        public boolean isConceptChanged(double value) {
            if(this.threshold > 0)
                return value > threshold;
            else
                return false;
        }
    }

    protected static class RobustThresholdConceptChangeDetector implements ConceptChangeDetector{
        private static final int NUM_SAMPLES = 6;

        private final double threshold;
        private final int windowSize;
        private List<Double> values;

        public RobustThresholdConceptChangeDetector(double threshold) {
            this(threshold, NUM_SAMPLES);
        }

        public RobustThresholdConceptChangeDetector(double threshold, int windowSize) {
            this.threshold = threshold;
            this.windowSize = windowSize;
            this.values =  new ArrayList<>();
        }

        @Override
        public boolean isConceptChanged(double value) {
            double referenceValue = value;
            if(threshold > 0) {
                values.add(value);
                if (values.size() >= windowSize) {
                    int index = Math.round(windowSize / 2);
                    if (index >= 0 && index < values.size()) {
                        referenceValue = values.get(index);
                    }
                    values.clear();
                } else {
                    return false;
                }
            } else {
                return false;
            }
            return referenceValue > threshold;
        }
    }
}

package bitflow4j.steps.normalization;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;
import bitflow4j.misc.Pair;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by anton on 6/23/16.
 */
public abstract class AbstractOnlineScaler extends AbstractPipelineStep {

    private static final Logger logger = Logger.getLogger(AbstractOnlineScaler.class.getName());

    public interface ConceptChangeHandler {
        // Return list must have same length as the changedFeatures input list. Each boolean indicates,
        // whether the updated min/max values for the feature at that index should actually be used for scaling from now on.
        // A false value at an index means the new min/max values will be dropped.
        //
        // As a shortcut, a return value of null equals a list of false values. If the returned list is shorted than the input
        // list, the missing values default to true (meaning, an empty list accepts all changes).
        List<Boolean> conceptChanged(AbstractOnlineScaler scaler, List<String> changedFeatures);
    }

    public interface ConceptChangeDetector {
        boolean isConceptChanged(double value);

        static ConceptChangeDetector getInstance(String type, double threshold) {
            switch (type) {
                case "threshold":
                    return new OnlineAutoMinMaxScaler.ThresholdConceptChangeDetector(threshold);
                case "robustThreshold":
                    return new OnlineAutoMinMaxScaler.RobustThresholdConceptChangeDetector(threshold);
                default:
                    throw new IllegalArgumentException(String.format("Unknown concept detector type %s", type));

            }
        }
    }

    public static ConceptChangeHandler acceptAllChangedConcepts(final boolean accept) {
        return (s, f) -> acceptChangedConcept(accept);
    }

    public static List<Boolean> acceptChangedConcept(boolean accept) {
        return accept ? Collections.emptyList() : null;
    }

    private Set<String> warnedMetrics = new HashSet<>();
    private ConceptChangeHandler handler;
    private ConceptChangeDetector detector;

    public AbstractOnlineScaler() {
        // By default, accept changed scaling models immediately.
        this(acceptAllChangedConcepts(true), (value) -> false);

    }

    public AbstractOnlineScaler(ConceptChangeHandler handler) {
        this(handler, (value) -> false);
    }

    public AbstractOnlineScaler(ConceptChangeHandler handler, ConceptChangeDetector detector) {
        this.handler = handler;
        this.detector = detector;
    }

    public void setConceptChangeHandler(ConceptChangeHandler handler) {
        this.handler = handler;
    }

    public void setConceptChangeHandler(ConceptChangeDetector detector) {
        this.detector = detector;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        double[] inMetrics = sample.getMetrics();
        String[] fields = sample.getHeader().header;
        double[] metrics = new double[inMetrics.length];
        List<String> changedFeatures = new ArrayList<>();
        for (int i = 0; i < inMetrics.length; i++) {
            String fieldName = fields[i];
            Pair<Double, Boolean> scaled = doScale(fieldName, inMetrics[i]);
            metrics[i] = scaled.getLeft();
            if (scaled.getRight()) {
                changedFeatures.add(fieldName);
            }
        }
        if (!changedFeatures.isEmpty() && handler != null) {
            List<Boolean> acceptChanges = handler.conceptChanged(this, changedFeatures);
            if (acceptChanges != null) {
                for (int i = 0; i < changedFeatures.size(); i++) {
                    // Treat missing values as truth-values
                    if (acceptChanges.size() <= i || acceptChanges.get(i)) {
                        updateScaling(changedFeatures.get(i));
                    }
                }
            }
        }
        super.writeSample(new Sample(sample.getHeader(), metrics, sample));
    }

    private Pair<Double, Boolean> doScale(String name, double val) {
        if (!canScale(name)) {
            if (!warnedMetrics.contains(name)) {
                warnedMetrics.add(name);
                logger.warning("WARNING: Missing scaling information for metric " + name + ", not scaling!");
            }
            return new Pair<>(val, false);
        }
        return scale(name, val, detector);
    }

    public ConceptChangeDetector getDetector() {
        return detector;
    }

    protected abstract boolean canScale(String name);

    // Returns the standardized value, and a boolean indicating whether the model used for scaling has changed.
    // If true is returned, the ConceptChangeHandler will be notified, and updateScaling() will be called, if the concept
    // change should be accepted. If updateScaling is not called, the updated scaling model should be dropped.
    // If an implementations does not support this, false should always be returned here, and updateScaling() should be implemented as an empty method.
    protected abstract Pair<Double, Boolean> scale(String name, double val, ConceptChangeDetector conceptChangeDetector);

    protected abstract void updateScaling(String name);

}

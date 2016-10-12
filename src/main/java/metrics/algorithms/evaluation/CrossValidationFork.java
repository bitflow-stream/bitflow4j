package metrics.algorithms.evaluation;

import metrics.Sample;
import metrics.io.fork.TwoWayFork;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * A portion of the incoming samples is forwarded to the first output. Only samples with certain "normal" labels are used.
 * The rest of these samples with "normal" labels are forwarded into the second output.
 * An equal number of samples with OTHER labels are also added to the second output.
 * Rationale: training only happens for certain labels, other labels are only used for validation.
 *
 * Created by anton on 9/6/16.
 */
public class CrossValidationFork extends TwoWayFork {

    private static final Logger logger = Logger.getLogger(CrossValidationFork.class.getName());

    // Batch of samples per label, for flushing into the output stream when this fork is closed.
    Map<String, Collection<Sample>> labeledSamples = new HashMap<>();

    private final Set<String> normalLabels;

    private double trainedSamples = 0.0;
    private final double trainingSamplePortion;
    private long validationSamples = 0;

    /**
     * @param normalLabels Samples with these labels will be forwarded directly (as training or validation sample).
     *                     Other labels will be stored and flushed when this fork is closed.
     */
    public CrossValidationFork(Set<String> normalLabels, double trainingSamplePortion) {
        this.normalLabels = normalLabels;
        this.trainingSamplePortion = trainingSamplePortion;
    }

    public void writeSample(Sample sample) throws IOException {
        boolean normalLabel = normalLabels != null && normalLabels.contains(sample.getLabel());
        if (!normalLabel) {
            String label = sample.getLabel();
            Collection<Sample> samples = labeledSamples.get(label);
            if (samples == null) {
                samples = new ArrayList<>();
                labeledSamples.put(label, samples);
            }
            samples.add(sample);
        } else {
            trainedSamples += trainingSamplePortion;
            if (trainedSamples >= 1.0) {
                trainedSamples -= 1.0;
                getOutputStream(TwoWayFork.ForkType.Primary).writeSample(sample);
            } else {
                validationSamples++;
                getOutputStream(TwoWayFork.ForkType.Secondary).writeSample(sample);
            }
        }
    }

    @Override
    public void close() throws IOException {
        long samplesPerLabel = validationSamples / labeledSamples.size();
        logger.info("Closing CrossValidationFork, flushing " + (samplesPerLabel * labeledSamples.size()) +
                " additional samples with " + labeledSamples.size() + " labels");
        for (Collection<Sample> samples : labeledSamples.values()) {
            int i = 0;
            for (Sample sample : samples) {
                if (i++ >= samplesPerLabel) break;
                getOutputStream(TwoWayFork.ForkType.Secondary).writeSample(sample);
            }
        }
        super.close();
    }

}

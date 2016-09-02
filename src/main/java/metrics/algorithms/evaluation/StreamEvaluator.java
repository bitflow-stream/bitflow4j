package metrics.algorithms.evaluation;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.algorithms.clustering.ClusterConstants;
import metrics.io.MetricOutputStream;

import java.io.IOException;

/**
 * Simple statistic about a clustering or classification algorithm that is in the pipeline
 * before this algorithm. An artificial metric can be appended to outgoing samples, describing
 * the historical precision of this clustering/classification algorithm.
 *
 * Created by anton on 9/2/16.
 */
public abstract class StreamEvaluator extends AbstractAlgorithm {

    private final boolean extendSample;

    protected long correctPredictions = 0, wrongPredictions = 0;
    protected double overallPrecision = 0;

    // If extendSample is true, the overall precision will be added to outgoing samples
    public StreamEvaluator(boolean extendSample) {
        this.extendSample = extendSample;
    }

    @Override
    public String toString() {
        return "moa stream evaluator";
    }

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        Boolean correct = isCorrectPrediction(sample);
        if (correct != null) {
            if (correct) {
                correctSample(sample);
            } else {
                incorrectSample(sample);
            }
            if (shouldRecalculate()) {
                recalculate();
            }
        }
        if (extendSample) {
            sample = sample.extend(new String[] { ClusterConstants.OVERALL_PRECISION_METRIC}, new double[] { overallPrecision });
        }
        return sample;
    }

    protected boolean shouldRecalculate() {
        return true;
    }

    protected void correctSample(Sample sample) {
        correctPredictions++;
    }

    protected void incorrectSample(Sample sample) {
        wrongPredictions++;
    }

    // Return true, if sample is predicted correctly, false if it is not.
    // Return null, if the sample is incomplete, an evaluation is not possible or should be avoided.
    protected abstract Boolean isCorrectPrediction(Sample sample);

    private void recalculate() {
        overallPrecision = (double) correctPredictions / (double) (correctPredictions + wrongPredictions);
    }

    @Override
    protected void inputClosed(MetricOutputStream output) throws IOException {
        recalculate();
        super.inputClosed(output);
    }

}

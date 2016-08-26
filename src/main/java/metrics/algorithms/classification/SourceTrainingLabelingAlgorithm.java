package metrics.algorithms.classification;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.algorithms.BatchAlgorithm;
import metrics.algorithms.WindowBatchAlgorithm;
import metrics.algorithms.clustering.ClusterConstants;
import metrics.io.MetricOutputStream;
import metrics.io.window.AbstractSampleWindow;
import metrics.io.window.SampleWindow;

import java.io.IOException;

/**
 * This algorithm should be used, when the performance of a classification algorithm should be evaluated using the {@link WekaEvaluationWrapper} algorithm.
 * This algorithm will add the label that should be predicted for the evaluator. This algorithm requires the SrcClsMapper to be configured correctly.
 */

public class SourceTrainingLabelingAlgorithm extends AbstractAlgorithm {

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        //TODO: maybe replace with src ?
        sample.setTag(ClusterConstants.EXPECTED_PREDICTION_TAG, SrcClsMapper.getCorrectPrediction(sample.getLabel()));
        return sample;
    }

    @Override
    public String toString() {
        return "source training labeling algorithm";
    }
}

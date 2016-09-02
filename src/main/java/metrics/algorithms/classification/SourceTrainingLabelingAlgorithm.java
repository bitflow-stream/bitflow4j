package metrics.algorithms.classification;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.algorithms.clustering.ClusterConstants;

import java.io.IOException;

/**
 * This algorithm should be used, when the performance of a classification algorithm should be evaluated using the {@link WekaEvaluationWrapper} algorithm.
 * This algorithm will add the label that should be predicted for the evaluator. This algorithm requires the SrcClsMapper to be configured correctly.
 */
public class SourceTrainingLabelingAlgorithm extends AbstractAlgorithm {

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        //TODO: maybe replace with src ?
        String s = SrcClsMapper.getCorrectPrediction(sample.getLabel());
//        if(s == null || s.isEmpty() || s.equals("null")){
//            System.out.println("break");
//        } TODO remove
        sample.setTag(ClusterConstants.EXPECTED_PREDICTION_TAG, s);
        if(sample.getLabel().equals("load")){
            sample.setLabel("idle");
        }
        return sample;
    }

    @Override
    public String toString() {
        return "source training labeling algorithm";
    }
}

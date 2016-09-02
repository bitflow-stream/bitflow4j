package metrics.algorithms.evaluation;

import metrics.Sample;
import metrics.algorithms.clustering.ClusterConstants;

import java.util.Collection;

/**
 * Evaluator for predicting one of two classes for every Sample: is it "normal" or "abnormal".
 * The evaluation is triggered only for samples that entered the pipeline with a specific label.
 * This allows to control the evaluation at runtime, useful for long-running online systems.
 *
 * Created by anton on 9/2/16.
 */
public class OnlineOutlierEvaluator extends StreamEvaluator {

    private final Collection<String> normalLabels;
    private final String expectNormalLabel;
    private final String expectAbnormalLabel;

    public OnlineOutlierEvaluator(boolean extendSample, Collection<String> normalLabels,
                                  String expectNormalLabel, String expectAbnormalLabel) {
        super(extendSample);
        this.normalLabels = normalLabels;
        this.expectNormalLabel = expectNormalLabel;
        this.expectAbnormalLabel = expectAbnormalLabel;
    }

    @Override
    protected Boolean isCorrectPrediction(Sample sample) {
        if (!sample.hasTag(ClusterConstants.ORIGINAL_LABEL_TAG)) return null;
        String originalLabel = sample.getTag(ClusterConstants.ORIGINAL_LABEL_TAG);
        boolean shouldBeNormal = originalLabel.equals(expectNormalLabel);
        boolean shouldBeAbnormal = originalLabel.equals(expectAbnormalLabel);
        if (!shouldBeNormal && !shouldBeAbnormal) return null;
        if (!sample.hasLabel()) return null;

        boolean isNormal = normalLabels.contains(sample.getLabel());
        return shouldBeNormal == isNormal;
    }

}

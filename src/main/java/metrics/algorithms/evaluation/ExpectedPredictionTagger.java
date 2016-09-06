package metrics.algorithms.evaluation;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.algorithms.clustering.ClusterConstants;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This algorithm should be used, when the performance of a classification algorithm should be evaluated using the {@link WekaEvaluationWrapper} algorithm.
 * This algorithm will add the label that should be predicted for the evaluator. This algorithm requires the SrcClsMapper to be configured correctly.
 */
public class ExpectedPredictionTagger extends AbstractAlgorithm {

    private Map<String, String> mapping = new ConcurrentHashMap<>();

    /**
     * If this is not null, this label will be used as expected tag when the incoming Sample has a label
     * that is not found in the {@link ExpectedPredictionTagger#mapping} map.
     * If this is null, such Samples will use their original label as expected tag.
     */
    public String defaultLabel = null;

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        String expected = null;
        if (sample.hasLabel()) {
            String label = sample.getLabel();
            if (mapping.containsKey(label)) {
                expected = mapping.get(label);
            } else if (defaultLabel == null) {
                expected = label;
            }
        }

        if (expected == null && defaultLabel != null)
            expected = defaultLabel;
        if (expected != null)
            sample.setTag(ClusterConstants.EXPECTED_PREDICTION_TAG, expected);
        return sample;
    }

    /**
     * Adds a single src-to-cls mapping. Be careful, as this method will silently replace old mappings.
     * @param src the src-label
     * @param cls the cls-label to predict
     * @throws IllegalArgumentException If a parameter is null
     */
    public void addMapping(String src, String cls) throws IllegalArgumentException{
        if (src == null || cls == null) throw new IllegalArgumentException("Null arguments provided");
        mapping.put(src, cls);
    }

    @Override
    public String toString() {
        return "expected prediction tagger";
    }

}

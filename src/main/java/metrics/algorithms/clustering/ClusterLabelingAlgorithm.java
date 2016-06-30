package metrics.algorithms.clustering;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Malcolm-X on 27.06.2016.
 */
public class ClusterLabelingAlgorithm extends AbstractAlgorithm {

    private int sampleCount;
    private Map<String, Integer> labelToClusterId;
    private Map<Integer, ClusterCounters> clusterIdToCounter;
    private ClusterCounter clusterCounter;

    public ClusterLabelingAlgorithm(double thresholdToClassify) {
        this.labelToClusterId = new HashMap<>();
        this.clusterIdToCounter = new HashMap<>();
        this.clusterCounter = new ClusterCounter(thresholdToClassify);
    }

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        String[] labelSplit;
        String labelPrefix;
        int labelClusterId;
        String originalLabel;

        try {
//            labelSplit = sample.getLabel().split(ClusterConstants.LABEL_SEPARATOR);
//            labelClusterId = Integer.valueOf(labelSplit[1]);
//            labelPrefix = labelSplit[0];
            labelClusterId = Integer.parseInt(sample.getTag(ClusterConstants.CLUSTER_TAG));
            originalLabel = sample.getTag(ClusterConstants.ORIGINAL_LABEL_TAG);
        } catch (NullPointerException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            throw new IOException("Sample not prepared for labeling, add a clusterer to the pipeline or fix current clusterer (failed to extract cluster id from point label or original label not found).");
        }
        clusterCounter.increment(labelClusterId, originalLabel);
        Sample sampleToReturn = new Sample(sample.getHeader(), sample.getMetrics(), sample.getTimestamp(), sample.getSource(), clusterCounter.calculateLabel(labelClusterId));
        sampleToReturn.setTag(ClusterConstants.ORIGINAL_LABEL_TAG, originalLabel);
        sampleToReturn.setTag(ClusterConstants.CLUSTER_TAG, String.valueOf(labelClusterId));
        return sampleToReturn;
    }

    @Override
    public String toString() {
        return "cluster labeling algorithm";
    }
}

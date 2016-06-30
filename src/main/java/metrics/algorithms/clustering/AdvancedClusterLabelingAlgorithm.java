package metrics.algorithms.clustering;

import com.google.common.primitives.Doubles;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import metrics.Header;

/**
 * This labeling algorithm extends the metrics with the inclusion probability to given labels. 
 * Created by fschmidt on 29.06.2016.
 */
public class AdvancedClusterLabelingAlgorithm extends AbstractAlgorithm {

    private final ClusterCounter clusterCounter;
    public static final String INC_PROB_PREFIX ="_inclusion_prob_label_";

    public AdvancedClusterLabelingAlgorithm(double thresholdToClassify) {
        this.clusterCounter = new ClusterCounter(thresholdToClassify);
    }

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        int labelClusterId;
        String originalLabel;

        try {
            labelClusterId = Integer.parseInt(sample.getTag(ClusterConstants.CLUSTER_TAG));
            originalLabel = sample.getTag(ClusterConstants.ORIGINAL_LABEL_TAG);
        } catch (NullPointerException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            throw new IOException(
                    "Sample not prepared for labeling, add a clusterer to the pipeline or fix current clusterer (failed to extract cluster id from point label or original label not found).");
        }
        clusterCounter.increment(labelClusterId, originalLabel);
        //Extend Header
        String[] headerNames = sample.getHeader().header;
        List<String> headerNamesList = new ArrayList<>(Arrays.asList(headerNames));
        Set<String> additionalHeaderNames = new HashSet<>(clusterCounter.getLabelInclusionProbability(labelClusterId).keySet());
        List<String> prefixAdditionalHeaderNames = additionalHeaderNames.stream().map(c -> INC_PROB_PREFIX + c).collect(Collectors.toList());
        headerNamesList.addAll(prefixAdditionalHeaderNames);
        Header header = new Header(headerNamesList.toArray(new String[headerNamesList.size()]), sample.getHeader());

        //Extend Metrics
        double[] metrics = sample.getMetrics();
        List<Double> metricsList = new ArrayList<>(Doubles.asList(metrics));
        for (int i = sample.getHeader().header.length; i < sample.getHeader().header.length + clusterCounter.getLabelInclusionProbability(
                labelClusterId).keySet().size(); i++) {
            String headerName = header.header[i].replace(INC_PROB_PREFIX, "");
            Double inclusionProbability = clusterCounter.getLabelInclusionProbability(labelClusterId).get(headerName);
            metricsList.add(inclusionProbability);
        }
        double[] extendedMetrics = Doubles.toArray(metricsList);

        Sample sampleToReturn = new Sample(header, extendedMetrics, sample.getTimestamp(), sample.getSource(), originalLabel);
        sampleToReturn.setTag(ClusterConstants.ORIGINAL_LABEL_TAG, originalLabel);
        sampleToReturn.setTag(ClusterConstants.CLUSTER_TAG, String.valueOf(labelClusterId));
        
        return sampleToReturn;
    }

    @Override
    public String toString() {
        return "advanced cluster labeling algorithm";
    }
}

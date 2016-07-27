package metrics.algorithms.evaluation;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.algorithms.clustering.ClusterConstants;

import java.io.IOException;

/**
 * Created by Malcolm-X on 27.07.2016.
 */
public class DistancePrinter extends AbstractAlgorithm {

    @Override
    public String toString() {
        return "distance printer";
    }

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        sample.getTags().entrySet().forEach(entry -> {
            if (entry.getKey().startsWith(ClusterConstants.DISTANCE_PREFIX)) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        });

        return sample;
    }
}

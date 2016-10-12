package metrics.main;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;
import metrics.algorithms.clustering.ClusterConstants;

import java.io.IOException;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

/**
 * Created by Malcolm-X on 27.07.2016.
 */
public class DistancePrinter extends AbstractAlgorithm {

    private static final Logger logger = getLogger(DistancePrinter.class.getName());

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        sample.getTags().entrySet().forEach(entry -> {
            if (entry.getKey().startsWith(ClusterConstants.DISTANCE_PREFIX)) {
                logger.info(entry.getKey() + " : " + entry.getValue());
            }
        });

        return sample;
    }
}

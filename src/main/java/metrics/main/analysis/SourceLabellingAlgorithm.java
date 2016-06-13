package metrics.main.analysis;

import metrics.Sample;
import metrics.algorithms.LabellingAlgorithm;

/**
 * Created by anton on 5/7/16.
 */
public class SourceLabellingAlgorithm extends LabellingAlgorithm {

    @Override
    protected String newLabel(Sample sample) {
        return sample.getSource();
    }

}

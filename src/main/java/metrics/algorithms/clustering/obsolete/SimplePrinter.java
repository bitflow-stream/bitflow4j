package metrics.algorithms.clustering.obsolete;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Malcolm-X on 16.09.2016.
 */
public class SimplePrinter extends AbstractAlgorithm {
    private int count;


    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        count++;
        if (count == 1) {
            System.out.println("printing all samples");
            System.out.println("Header");
            System.out.println(Arrays.toString(sample.getHeader().header));
        }
        System.out.println(count + "|" + Arrays.toString(sample.getMetrics()));
        return sample;
    }
}

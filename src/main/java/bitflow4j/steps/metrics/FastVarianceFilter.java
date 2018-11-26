package bitflow4j.steps.metrics;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Header;
import bitflow4j.Sample;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author fschmidt
 */
public class FastVarianceFilter extends AbstractPipelineStep {

    private DescriptiveStatistics[] stats;
    private boolean firstSample = true;
    private final double minVar;
    private final List<Sample> samples = new ArrayList<>();
    private String[] headerNames;

    public FastVarianceFilter(double minVar) {
        this.minVar = minVar;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        //init structures with first sample
        if (firstSample) {
            stats = new DescriptiveStatistics[sample.getMetrics().length];
            for (int i = 0; i < sample.getMetrics().length; i++) {
                stats[i] = new DescriptiveStatistics();
            }
            headerNames = sample.getHeader().header;
            firstSample = false;
        }

        for (int i = 0; i < sample.getMetrics().length; i++) {
            double metric = sample.getMetrics()[i];
            stats[i].addValue(metric);
        }
        samples.add(sample);
    }

    @Override
    protected void doClose() throws IOException {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < stats.length; i++) {
            double var = stats[i].getVariance();
            if (var >= minVar) {
                ids.add(i);
            }
        }
        String[] newH = new String[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            newH[i] = headerNames[ids.get(i)];
        }
        Header newHeader = new Header(newH);
        for (Sample sample : samples) {
            double[] newM = new double[ids.size()];
            for (int i = 0; i < ids.size(); i++) {
                newM[i] = sample.getMetrics()[ids.get(i)];
            }
            Sample newSample = new Sample(newHeader, newM, sample);
            output.writeSample(newSample);
        }
        super.doClose();
    }

}

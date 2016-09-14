package metrics.algorithms.clustering;

import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;

import java.io.IOException;

/**
 * Created by malcolmx on 14.09.16.
 */
public class PrepareClusterForPlot extends AbstractAlgorithm {

    private int[] fieldsToPlot;
    private boolean useLastMetricAsRadius = false;

    public PrepareClusterForPlot(int[] fieldsToPlot) {
        if (fieldsToPlot == null || fieldsToPlot.length == 0) throw new IllegalArgumentException();
        this.fieldsToPlot = fieldsToPlot;
    }

    public PrepareClusterForPlot() {
        this(new int[] {0,1});
    }

    public void useLastMetricAsRadius(){
        this.useLastMetricAsRadius = true;
    }

    @Override
    protected synchronized Sample executeSample(Sample sample) throws IOException {
        Sample result = null;
        int numOfMetrics = fieldsToPlot.length + (useLastMetricAsRadius ? 1 : 0);
        String[] headerStrings = new String[numOfMetrics];
        String[] oldHeader = sample.getHeader().header;
        double[] newMetrics = new double[numOfMetrics];
        double[] oldMetrics = sample.getMetrics();
        for (int i = 0; i < fieldsToPlot.length; i++){
            headerStrings[i] = oldHeader[fieldsToPlot[i]];
            newMetrics[i] = oldMetrics[fieldsToPlot[i]];
        }
        if(useLastMetricAsRadius) {
            headerStrings[fieldsToPlot.length] = oldHeader[oldHeader.length -1];
            newMetrics[fieldsToPlot.length] = oldMetrics[oldMetrics.length -1];
        }
        if (result == null) throw new IOException();
        return result;
    }
}

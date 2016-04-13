package metrics.algorithms;

import Jama.Matrix;
import com.mkobos.pca_transform.PCA;
import metrics.Sample;
import metrics.io.MetricOutputStream;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by anton on 4/13/16.
 *
 * Perform a PCA training on the entire dataset, then apply the resulting
 * model to the entire dataset and output the results;
 */
public class PCAAlgorithm extends PostAnalysisAlgorithm<CorrelationAlgorithm.NoNanMetricLog> {

    private final int trainingSamples;

    // If trainingSamples is > 0, only the beginning of the input is used for training the PCA model.
    // Afterwards, the entire data set is transformed.
    public PCAAlgorithm(int trainingSamples) {
        super(true);
        this.trainingSamples = trainingSamples;
    }

    @Override
    protected void writeResults(MetricOutputStream output) throws IOException {
        double[][] dataset = getSampleMatrix();
        int trainingSize = trainingSamples > 0 ? trainingSamples : dataset.length;
        if (trainingSize > dataset.length) trainingSize = dataset.length;
        double[][] trainingSet = Arrays.copyOf(dataset, trainingSize);
        PCA pca = new PCA(new Matrix(trainingSet));

        System.err.println("PCA model computed, now transforming input data...");
        Matrix transformMatrix = new Matrix(dataset);
        Matrix transformed = pca.transform(transformMatrix, PCA.TransformationType.WHITENING);
        outputValues(transformed.getArray(), output);
    }

    private void outputValues(double[][] values, MetricOutputStream output) throws IOException {
        if (values.length == 0) {
            System.err.println(toString() + " produced no output");
            return;
        }

        // Create header for PCA values
        int numCols = values[0].length;
        String[] headerFields = new String[numCols];
        for (int i = 0; i < numCols; i++) {
            headerFields[i] = "component" + i;
        }
        Sample.Header header = new Sample.Header(headerFields, Sample.Header.TOTAL_SPECIAL_FIELDS);

        // Output values
        for (int i = 0; i < values.length; i++) {
            SampleMetadata meta = samples.get(i);
            Sample sample = new Sample(header, values[i], meta.timestamp, meta.source, meta.label);
            output.writeSample(sample);
        }
        if (values.length != samples.size()) {
            System.err.println("Warning: output " + values.length + " samples, but input contained " + samples.size() + " samples");
        }
    }

    private double[][] getSampleMatrix() {
        int rows = samples.size();
        double matrix[][] = new double[rows][];
        for (int sampleNr = 0; sampleNr < rows; sampleNr++) {
            matrix[sampleNr] = getSampleValues(sampleNr);
        }
        return matrix;
    }

    private double[] getSampleValues(int sampleNr) {
        double row[] = new double[metrics.size()];
        int metricNr = 0;
        for (MetricLog metricLog : metrics.values()) {
            row[metricNr] = metricLog.getValue(sampleNr);
            metricNr++;
        }
        return row;
    }

    @Override
    protected CorrelationAlgorithm.NoNanMetricLog createMetricStats(String name) {
        return new CorrelationAlgorithm.NoNanMetricLog(name);
    }

    @Override
    public String toString() {
        return "pca algorithm [" + trainingSamples + "]";
    }

}

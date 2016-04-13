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

    private double accuracy;

    // If trainingSamples is > 0, only the beginning of the input is used for training the PCA model.
    // Afterwards, the entire data set is transformed.
    public PCAAlgorithm(int trainingSamples) {
        this(trainingSamples,0.99);
    }

    public PCAAlgorithm(int trainingSamples, double accuracy){
        super(true);
        this.accuracy = accuracy;
        this.trainingSamples = trainingSamples;
    }


    @Override
    protected void writeResults(MetricOutputStream output) throws IOException {
        double[][] dataset = getSampleMatrix();
        int trainingSize = trainingSamples > 0 ? trainingSamples : dataset.length;
        if (trainingSize > dataset.length) trainingSize = dataset.length;
        double[][] trainingSet = Arrays.copyOf(dataset, trainingSize);
        PCA pca = new PCA(new Matrix(trainingSet));

        int eigenLength = calcEigenLength(pca);

        System.err.println("PCA model computed, now transforming input data to " + eigenLength + " dimensions...");
        Matrix transformMatrix = new Matrix(dataset);
        Matrix transformed = pca.transform(transformMatrix, PCA.TransformationType.ROTATION);
        outputValues(transformed.getArray(), eigenLength, output);
    }

    private void outputValues(double[][] values, int numCols, MetricOutputStream output) throws IOException {
        if (values.length == 0) {
            System.err.println(toString() + " produced no output");
            return;
        }

        // Create header for PCA values
        String[] headerFields = new String[numCols];
        for (int i = 0; i < numCols; i++) {
            headerFields[i] = "component" + i;
        }
        Sample.Header header = new Sample.Header(headerFields, Sample.Header.TOTAL_SPECIAL_FIELDS);

        // Output values
        for (int i = 0; i < values.length; i++) {
            SampleMetadata meta = samples.get(i);
            double[] vector = values[i];
            if (numCols < vector.length)
                vector = Arrays.copyOf(vector, numCols);
            Sample sample = new Sample(header, vector, meta.timestamp, meta.source, meta.label);
            output.writeSample(sample);
        }
        if (values.length != samples.size()) {
            System.err.println("Warning: output " + values.length + " samples, but input contained " + samples.size() + " samples");
        }
    }

    private int calcEigenLength(PCA pca){
        int eigenLength=0;
        double eigenSum = 0;
        for ( int i =0; i < pca.getOutputDimsNo();i++){
                eigenSum += pca.getEigenvalue(i);
        }
        double eigenVar = 0.00;
        while (eigenVar < this.accuracy) {
            if( eigenLength < pca.getOutputDimsNo()) {
                eigenVar += pca.getEigenvalue(eigenLength) / eigenSum;
                eigenLength += 1;
            }else {
                System.err.println("accuracy not reached, using " + eigenSum + " instead");
            }
        }
        return eigenLength;
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

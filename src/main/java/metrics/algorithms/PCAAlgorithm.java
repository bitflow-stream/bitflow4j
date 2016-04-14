package metrics.algorithms;

import Jama.Matrix;
import com.mkobos.pca_transform.PCA;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import metrics.Sample;
import metrics.io.MetricOutputStream;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by anton on 4/13/16.
 * <p>
 * Perform a PCA training on the entire dataset, then apply the resulting
 * model to the entire dataset and output the results;
 */
public class PCAAlgorithm extends PostAnalysisAlgorithm<CorrelationAlgorithm.NoNanMetricLog> {

    private static final double REPORT_VARIANCE = 0.99;
    private static final PCA.TransformationType TRANSFORMATION_TYPE = PCA.TransformationType.ROTATION;

    // If trainingSamples is > 0, only the beginning of the input is used for training the PCA model.
    // Afterwards, the entire data set is transformed.
    private final int trainingSamples;

    private final double minContainedVariance;
    private final int maxDimensions;

    public PCAAlgorithm() {
        this(-1, 0.99);
    }

    public PCAAlgorithm(int maxDimensions) {
        this(-1, maxDimensions);
    }

    public PCAAlgorithm(double minContainedVariance) {
        this(-1, minContainedVariance);
    }

    public PCAAlgorithm(int trainingSamples, double minContainedVariance) {
        super(true);
        this.minContainedVariance = minContainedVariance;
        this.maxDimensions = Integer.MAX_VALUE;
        this.trainingSamples = trainingSamples;
    }

    public PCAAlgorithm(int trainingSamples, int maxDimensions) {
        super(true);
        this.minContainedVariance = Double.MAX_VALUE;
        this.maxDimensions = maxDimensions;
        this.trainingSamples = trainingSamples;
    }

    @Override
    protected void writeResults(MetricOutputStream output) throws IOException {
        double[][] dataset = getSampleMatrix();
        int trainingSize = trainingSamples > 0 ? trainingSamples : dataset.length;
        if (trainingSize > dataset.length) trainingSize = dataset.length;
        double[][] trainingSet = Arrays.copyOf(dataset, trainingSize);
        PCA pca = new PCA(new Matrix(trainingSet), true);

        TransformationParameters params = new TransformationParameters(pca, maxDimensions, minContainedVariance);
        params.printMessage(REPORT_VARIANCE);
        Matrix transformed = pca.transform(new Matrix(dataset), TRANSFORMATION_TYPE);
        outputValues(transformed.getArray(), params.dimensions, output);
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

    private static class TransformationParameters {
        final double containedVariance;
        final int dimensions;
        final TDoubleList variances;

        TransformationParameters(PCA pca, int maxDimensions, double minContainedVariance) {
            int totalDimensions = pca.getOutputDimsNo();
            variances = new TDoubleArrayList(totalDimensions);

            double eigenvalueSum = 0;
            for (int i = 0; i < totalDimensions; i++) {
                double eigenvalue = pca.getEigenvalue(i);
                eigenvalueSum += eigenvalue;
                variances.add(eigenvalue);
            }

            int dimensions = 0;
            double containedVariance = 0;
            for (int i = 0; i < totalDimensions; i++) {
                double variance = variances.get(i) / eigenvalueSum;
                variances.set(i, variance);
                if (dimensions < maxDimensions && containedVariance < minContainedVariance) {
                    dimensions++;
                    containedVariance += variance;
                }
            }
            this.dimensions = dimensions;
            this.containedVariance = containedVariance;
        }

        void printMessage(double reportVariance) {
            System.err.println("PCA model computed, now transforming input data to "
                    + dimensions + " dimension(s) with " + containedVariance + " variance contained...");

            double cumulative = 0.0;
            int i = 0;
            System.err.print("Variances per component: ");
            do {
                double variance = variances.get(i);
                cumulative += variance;
                if (i > 0) System.err.print(", ");
                System.err.printf("%d: %f", i, variance);
                i++;
            } while (cumulative < reportVariance && i < variances.size() - 1);
            System.err.println();
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
        StringBuilder str = new StringBuilder("pca algorithm [");
        boolean hasInfo = false;
        if (trainingSamples > 0) {
            str.append(trainingSamples).append(" trainingset");
            hasInfo = true;
        }
        if (minContainedVariance != Double.MAX_VALUE) {
            if (hasInfo) str.append(", ");
            str.append(minContainedVariance).append(" variance");
            hasInfo = true;
        }
        if (maxDimensions != Integer.MAX_VALUE) {
            if (hasInfo) str.append(", ");
            str.append(maxDimensions).append(" dimensions");
        }
        return str.append("]").toString();
    }

}

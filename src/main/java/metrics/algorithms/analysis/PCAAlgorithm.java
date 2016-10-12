package metrics.algorithms.analysis;

import Jama.Matrix;
import com.mkobos.pca_transform.PCA;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import metrics.Header;
import metrics.Sample;
import metrics.algorithms.WindowBatchAlgorithm;
import metrics.io.MetricOutputStream;
import metrics.io.window.AbstractSampleWindow;
import metrics.io.window.SampleMetadata;
import metrics.io.window.SampleWindow;
import metrics.main.misc.ParameterHash;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Created by anton on 4/13/16.
 * <p>
 * Perform a PCA training on the entire dataset, then apply the resulting
 * model to the entire dataset and output the results;
 */
public class PCAAlgorithm extends WindowBatchAlgorithm {

    private static final Logger logger = Logger.getLogger(PCAAlgorithm.class.getName());

    private static final double REPORT_VARIANCE = 0.999;

    // If trainingSamples is > 0, only the beginning of the input is used for training the PCA model.
    // Afterwards, the entire data set is transformed.
    private final int trainingSamples;

    private final double minContainedVariance;
    private final int minDimensions;
    private final boolean centerPca;
    private final PCA.TransformationType transformationType;

    // TODO optionally allow MultiHeaderWindow...
    private final AbstractSampleWindow window = new SampleWindow();

    public PCAAlgorithm(int minDimensions) {
        this(-1, minDimensions);
    }

    public PCAAlgorithm(double minContainedVariance) {
        this(-1, minContainedVariance);
    }

    public PCAAlgorithm(int trainingSamples, double minContainedVariance) {
        this(trainingSamples, Integer.MIN_VALUE, minContainedVariance);
    }

    public PCAAlgorithm(int trainingSamples, int minDimensions) {
        this(trainingSamples, minDimensions, Double.MIN_VALUE);
    }

    public PCAAlgorithm(int trainingSamples, int minDimensions, double minContainedVariance) {
        this(trainingSamples, minDimensions, minContainedVariance, true, PCA.TransformationType.ROTATION);
    }

    // If both minDimensions and minContainedVariance is given, then both conditions will be satisfied,
    // potentially "over-satisfying" one of them.
    public PCAAlgorithm(int trainingSamples, int minDimensions, double minContainedVariance, boolean center, PCA.TransformationType transformationType) {
        if (minContainedVariance >= 1)
            throw new IllegalArgumentException("Contained variance must be < 1: " + minContainedVariance);
        this.minContainedVariance = minContainedVariance;
        this.minDimensions = minDimensions;
        this.trainingSamples = trainingSamples;
        this.centerPca = center;
        this.transformationType = transformationType;
    }

    @Override
    protected AbstractSampleWindow getWindow() {
        return window;
    }

    @Override
    protected void flushResults(MetricOutputStream output) throws IOException {
        double[][] dataset = getSampleMatrix();
        int trainingSize = trainingSamples > 0 ? trainingSamples : dataset.length;
        if (trainingSize > dataset.length) trainingSize = dataset.length;
        double[][] trainingSet = Arrays.copyOf(dataset, trainingSize);
        PCA pca = new PCA(new Matrix(trainingSet), centerPca);

        TransformationParameters params = new TransformationParameters(pca, minDimensions, minContainedVariance);
        params.printMessage(REPORT_VARIANCE);
        Matrix transformed = pca.transform(new Matrix(dataset), transformationType);
        outputValues(transformed.getArray(), params.dimensions, output);
    }

    private void outputValues(double[][] values, int numCols, MetricOutputStream output) throws IOException {
        if (values.length == 0) {
            logger.warning(toString() + " produced no output");
            return;
        }

        // Create header for PCA values
        String[] headerFields = new String[numCols];
        for (int i = 0; i < numCols; i++) {
            headerFields[i] = "component" + i;
        }
        Header header = new Header(headerFields);

        // Output values
        for (int i = 0; i < values.length; i++) {
            SampleMetadata meta = window.getSampleMetadata(i);
            double[] vector = values[i];
            if (numCols < vector.length)
                vector = Arrays.copyOf(vector, numCols);
            Sample sample = meta.newSample(header, vector);
            output.writeSample(sample);
        }
        if (values.length != window.numSamples()) {
            logger.warning("Warning: output " + values.length + " samples, but input contained " + window.numSamples() + " samples");
        }
    }

    private static class TransformationParameters {
        final double containedVariance;
        final int dimensions;
        final TDoubleList variances;

        TransformationParameters(PCA pca, int minDimensions, double minContainedVariance) {
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
                if (dimensions < minDimensions || containedVariance < minContainedVariance) {
                    dimensions++;
                    containedVariance += variance;
                }
            }
            this.dimensions = dimensions;
            this.containedVariance = containedVariance;
        }

        void printMessage(double reportVariance) {
            logger.info("PCA model computed, now transforming input data to "
                    + dimensions + " dimension(s) with " + containedVariance + " variance contained...");

            double cumulative = 0.0;
            int i = 0;
            String info = "";
            do {
                double variance = variances.get(i);
                cumulative += variance;
                if (i > 0) info += ", ";
                info += i + ": " +  variance;
                i++;
            } while (cumulative < reportVariance && i < variances.size() - 1);
            logger.info("Variances per component: " + info);
        }
    }

    private double[][] getSampleMatrix() {
        int rows = window.numSamples();
        double matrix[][] = new double[rows][];
        for (int sampleNr = 0; sampleNr < rows; sampleNr++) {
            matrix[sampleNr] = window.getSampleValues(sampleNr);
        }
        return matrix;
    }

    @Override
    public void hashParameters(ParameterHash hash) {
        super.hashParameters(hash);
        hash.writeInt(trainingSamples);
        hash.writeDouble(minContainedVariance);
        hash.writeInt(minDimensions);
        hash.writeBoolean(centerPca);
        hash.writeInt(transformationType.ordinal());
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("pca algorithm [");
        boolean hasInfo = false;
        if (trainingSamples > 0) {
            str.append(trainingSamples).append(" trainingset");
            hasInfo = true;
        }
        if (minContainedVariance > 0) {
            if (hasInfo) str.append(", ");
            str.append(minContainedVariance).append(" variance");
            hasInfo = true;
        }
        if (minDimensions > 0) {
            if (hasInfo) str.append(", ");
            str.append(minDimensions).append(" dimensions");
        }
        return str.append("]").toString();
    }

}

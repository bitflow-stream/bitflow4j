package metrics.algorithms.analysis;

import com.jujutsu.tsne.FastTSne;
import com.jujutsu.tsne.TSne;
import metrics.Header;
import metrics.Sample;
import metrics.algorithms.WindowBatchAlgorithm;
import metrics.io.MetricOutputStream;
import metrics.io.window.*;

import java.io.IOException;
import java.util.Arrays;

/**
 * The T-SNE maps a high-dimensional dataset to a lower number of dimensions.
 * Based on initial experiments this is very slow and PCAAlgorithm can be used instead.
 *
 * Created by anton on 5/14/16.
 */
public class TsneAlgorithm extends WindowBatchAlgorithm {

    private final AbstractSampleWindow window;
    private final double perplexity;
    private final int initial_dims;
    private final int max_iter;
    private final boolean use_pca;

    public TsneAlgorithm(AbstractSampleWindow window, double perplexity, int initial_dims, int max_iter, boolean use_pca) {
        this.window = window;
        this.perplexity = perplexity;
        this.initial_dims = initial_dims;
        this.max_iter = max_iter;
        this.use_pca = use_pca;
    }

    public TsneAlgorithm(boolean changingHeader, double perplexity, int initial_dims, int max_iter, boolean use_pca) {
        this(changingHeader ? new MultiHeaderWindow<>(MetricWindow.FACTORY) : new SampleWindow(),
                perplexity, initial_dims, max_iter, use_pca);
    }

    public TsneAlgorithm(double perplexity, int initial_dims, int max_iter, boolean use_pca) {
        this(false, perplexity, initial_dims, max_iter, use_pca);
    }

    public TsneAlgorithm() {
        this(5.0, 50, 2000, false);
    }

    @Override
    protected void flushResults(MetricOutputStream output) throws IOException {
        double[][] dataset = getSampleMatrix();
        TSne tsne = new FastTSne();
        double[][] result = tsne.tsne(dataset, window.numMetrics(), initial_dims, perplexity, max_iter, use_pca);
        if (result.length == 0 || result[0].length == 0) {
            System.err.println(toString() + " produced no output");
            return;
        }
        outputValues(result, output);
    }

    private void outputValues(double[][] values, MetricOutputStream output) throws IOException {
        int numCols = values[0].length;

        // Create header for PCA values
        String[] headerFields = new String[numCols];
        for (int i = 0; i < numCols; i++) {
            headerFields[i] = "tsne" + i;
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
            System.err.println("Warning: output " + values.length + " samples, but input contained " + window.numSamples() + " samples");
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
    protected AbstractSampleWindow getWindow() {
        return window;
    }

}

package metrics.algorithms.analysis;

import metrics.algorithms.WindowBatchAlgorithm;
import metrics.algorithms.analysis.lasso.LassoFit;
import metrics.algorithms.analysis.lasso.LassoFitGenerator;
import metrics.io.MetricOutputStream;
import metrics.io.window.AbstractSampleWindow;
import metrics.io.window.MetricWindow;
import metrics.io.window.MultiHeaderWindow;
import metrics.main.misc.ParameterHash;

import java.io.IOException;

/**
 * TODO this is unfinished.
 *
 * Created by anton on 4/19/16.
 */
public class LassoAlgorithm extends WindowBatchAlgorithm {

    public final int maxAllowedFeatures;

    private final AbstractSampleWindow window = new MultiHeaderWindow<>(MetricWindow.FACTORY);

    public LassoAlgorithm(int maxAllowedFeatures) {
        this.maxAllowedFeatures = maxAllowedFeatures;
    }

    @Override
    protected AbstractSampleWindow getWindow() {
        return window;
    }

    @Override
    protected void flushResults(MetricOutputStream output) throws IOException {
        // two Lasso algorithms should be implemented: one for filtering out metrics
        // and a second one for the actual predictive model. The first one already trains the model,
        // the second one just needs it as input to generate predictions for future metrics.
        // TODO
        if (true)
            throw new UnsupportedOperationException("Lasso algorithm is not implemented yet");

        int featuresCount = window.numMetrics();
        int numObservations = window.numSamples();

        // Every Observsation needs one target
        double[][] observations = new double[numObservations][];
        double[] targets = new double[numObservations];

        LassoFitGenerator fitGenerator = new LassoFitGenerator();
        try {
            fitGenerator.init(featuresCount, numObservations);
        } catch (Exception e) {
            throw new IOException("Lasso initialization failed", e);
        }
        for (int i = 0; i < numObservations; i++) {
            fitGenerator.setObservationValues(i, observations[i]);
            fitGenerator.setTarget(i, targets[i]);
        }

        LassoFit fit = fitGenerator.fit(maxAllowedFeatures);

        // TODO
    }

    @Override
    public void hashParameters(ParameterHash hash) {
        super.hashParameters(hash);
        hash.writeInt(maxAllowedFeatures);
    }

}

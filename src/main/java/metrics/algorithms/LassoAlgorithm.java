package metrics.algorithms;

import metrics.algorithms.lasso.LassoFit;
import metrics.algorithms.lasso.LassoFitGenerator;
import metrics.io.MetricOutputStream;

import java.io.IOException;

/**
 * Created by anton on 4/19/16.
 */
public class LassoAlgorithm extends PostAnalysisAlgorithm<PostAnalysisAlgorithm.MetricLog> {

    public final int maxAllowedFeatures;

    public LassoAlgorithm(int maxAllowedFeatures) {
        super(true);
        this.maxAllowedFeatures = maxAllowedFeatures;
    }

    @Override
    public String toString() {
        return "lasso [" + maxAllowedFeatures + "]";
    }

    @Override
    protected void writeResults(MetricOutputStream output) throws IOException {

        // two Lasso algorithms should be implemented: one for filtering out metrics
        // and a second one for the actual predictive model. The first one already trains the model,
        // the second one just needs it as input to generate predictions for future metrics.
        // TODO
        if (true)
            throw new UnsupportedOperationException("Lasso algorithm is not implemented yet");

        int featuresCount = metrics.size();
        int numObservations = samples.size();

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
    protected MetricLog createMetricStats(String name) {
        return new MetricLog(name);
    }

}

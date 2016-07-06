package metrics.algorithms.clustering;

import metrics.io.window.MetricWindow;
import metrics.io.window.MetricWindowFactory;

/**
 * Created by fschmidt on 29.06.16.
 */
public class LabelInclusionProbabilityPredictionWindow extends MetricWindow {

    public static final MetricWindowFactory<LabelInclusionProbabilityPredictionWindow> FACTORY
            = LabelInclusionProbabilityPredictionWindow::new;
    private final double SMALL_VALUE = 0; // 0.0000000000001;

    public double runningSum = 0.0;

    public LabelInclusionProbabilityPredictionWindow(String name) {
        super(name);
    }

    @Override
    public void flushSamples(int numSamples) {
        for (int i = 0; i < numSamples; i++) {
            if (i >= values.size()) {
                break;
            }
            double val = values.get(i);
            if (val == 0.0) {
                val = SMALL_VALUE;
            }
            runningSum -= val;
        }
        super.flushSamples(numSamples);
    }

    @Override
    public void addImpl(double val) {
        super.addImpl(val);
        if (val == 0.0) {
            val = SMALL_VALUE;
        }
        runningSum += val;
    }
    
    @Override
    public void fill(int num) {
        for (int i = 0; i < num; i++) {
            addImpl(0.0);
        }
    }

    public double start() {
        if (values.isEmpty()) {
            return 0;
        }
        return values.get(0);
    }

    public double end() {
        if (values.isEmpty()) {
            return 0;
        }
        return values.get(values.size() - 1);
    }

    public double labelInclusionProbabilityAverage() {
        if (values.isEmpty()) {
            return 0.0;
        }
        return runningSum / (double) values.size();
    }

    @Override
    public void clear() {
        super.clear();
        runningSum = 0.0;
    }
}

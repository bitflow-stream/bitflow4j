package metrics.algorithms.clustering;

import metrics.io.window.*;

/**
 * Created by fschmidt on 29.06.16.
 */
public class LabelInclusionProbabilityPredictionWindow extends MetricWindow {

    public static final MetricWindowFactory<LabelInclusionProbabilityPredictionWindow> FACTORY
            = LabelInclusionProbabilityPredictionWindow::new;
    private final double SMALL_VALUE = 0.0000000000001;

    public double prod = 1.0;

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
            prod /= val;
        }
        super.flushSamples(numSamples);
    }

    @Override
    public void addImpl(double val) {
        super.addImpl(val);
        if (val == 0.0) {
            val = SMALL_VALUE;
        }
        prod *= val;
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
        double inclusionProbability = (double) prod / (double) values.size();
//        System.out.println(values);
//        System.out.println(prod);
//        System.out.println(values.size());
        return inclusionProbability;
    }

    @Override
    public void clear() {
        super.clear();
        prod = 0.0;
    }
}

package bitflow4j.steps.normalization;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Header;
import bitflow4j.Sample;
import bitflow4j.misc.MetricScaler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by anton on 4/18/16.
 */
public abstract class TrainedFeatureScaler extends AbstractPipelineStep {

    private static final Logger logger = Logger.getLogger(TrainedFeatureScaler.class.getName());

    private final Header.ChangeChecker header = new Header.ChangeChecker();
    private final List<Sample> trainingSamples;
    private List<MetricScaler> scalers = new ArrayList<>();

    private final int numTrainingSamples;
    private boolean trainingDone = false;

    public TrainedFeatureScaler(int numTrainingSamples) {
        this.numTrainingSamples = numTrainingSamples;
        trainingSamples = new ArrayList<>(numTrainingSamples);
    }

    protected abstract MetricScaler createScaler(List<Sample> sample, int metricIndex);

    @Override
    public void writeSample(Sample sample) throws IOException {
        if (header.changed(sample.getHeader())) {
            logger.info(this + ": Collecting " + numTrainingSamples + " training samples");
            restartTraining();
        }
        if (trainingDone) {
            sample = scaleInstance(sample);
        } else {
            trainingSamples.add(sample);
            if (trainingSamples.size() >= numTrainingSamples) {
                buildScalers();
                for (Sample trainingSample : trainingSamples) {
                    super.writeSample(scaleInstance(trainingSample));
                }
                trainingSamples.clear();
                trainingDone = true;
            }
        }
        super.writeSample(sample);
    }

    private void restartTraining() {
        trainingDone = false;
        trainingSamples.clear();
        scalers.clear();
    }

    private void buildScalers() {
        scalers.clear();
        for (int i = 0; i < header.lastHeader.header.length; i++) {
            scalers.add(createScaler(trainingSamples, i));
        }
    }

    protected Sample scaleInstance(Sample sample) throws IOException {
        Header header = sample.getHeader();
        double[] incoming = sample.getMetrics();
        double[] values = new double[header.header.length];
        for (int i = 0; i < header.header.length; i++) {
            MetricScaler scaler = scalers.get(i);
            if (scaler == null) {
                values[i] = incoming[i];
            } else {
                values[i] = scaler.scale(incoming[i]);
            }
        }
        return new Sample(header, values, sample);
    }

    public static class MinMax extends TrainedFeatureScaler {
        public MinMax(int numTrainingSamples) {
            super(numTrainingSamples);
        }

        @Override
        protected MetricScaler createScaler(List<Sample> sample, int metricIndex) {
            return new MetricScaler.MinMaxScaler(sample, metricIndex);
        }
    }

    public static class Standardize extends TrainedFeatureScaler {
        public Standardize(int numTrainingSamples) {
            super(numTrainingSamples);
        }

        @Override
        protected MetricScaler createScaler(List<Sample> sample, int metricIndex) {
            return new MetricScaler.MetricStandardizer(sample, metricIndex);
        }
    }

}

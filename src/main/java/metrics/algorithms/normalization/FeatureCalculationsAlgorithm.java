package metrics.algorithms.normalization;

import metrics.Header;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anton on 7/26/16.
 */
public class FeatureCalculationsAlgorithm extends AbstractAlgorithm {

    public interface FeatureCalculation {
        double calculate(FeatureAccess access);
    }

    private final Map<String, FeatureCalculation> calculations;
    private final FeatureAccess access = new FeatureAccess();

    public FeatureCalculationsAlgorithm(Map<String, FeatureCalculation> calculations) {
        this.calculations = calculations;
    }

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        access.updateHeader(sample.getHeader());
        access.setSample(sample);

        String newFields[] = new String[calculations.size()];
        double newValues[] = new double[newFields.length];
        int i = 0;
        for (Map.Entry<String, FeatureCalculation> entry : calculations.entrySet()) {
            double value;
            try {
                value = entry.getValue().calculate(access);
            } catch(IllegalArgumentException e) {
                System.err.println("Feature Calculation tried to access missing metric: " + e);
                value = 0;
            }
            newFields[i] = entry.getKey();
            newValues[i] = value;
            i++;
        }
        return sample.extend(newFields, newValues);
    }

    public class FeatureAccess {

        private Header lastHeader = null;
        private final Map<String, Integer> indices = new HashMap<>();
        private double values[];

        void updateHeader(Header header) {
            if (header.hasChanged(lastHeader)) {
                indices.clear();
                for (int i = 0; i < header.header.length; i++) {
                    indices.put(header.header[i], i);
                }
                lastHeader = header;
            }
        }

        void setSample(Sample sample) {
            values = sample.getMetrics();
        }

        public double getFeature(String name) {
            Integer result = indices.get(name);
            if (result == null) {
                throw new IllegalArgumentException("No such metric: " + name);
            }
            return values[result];
        }

    }

    @Override
    public String toString() {
        return "Feature Calculations Algorithm";
    }

}

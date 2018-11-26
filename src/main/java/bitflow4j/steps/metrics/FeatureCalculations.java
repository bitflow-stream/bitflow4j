package bitflow4j.steps.metrics;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Abstract pipeline step designed to extend incoming Samples by "artificial" features. Every artificial feature is represented by an
 * implementation of FeatureCalculation. Artificial features can be based on values of existing features, which are accessed through an
 * instance of FeatureAccess.
 * <p>
 * Created by anton on 7/26/16. Extended by alex on 05/06/17.
 */
public class FeatureCalculations extends AbstractPipelineStep {

    private static final Logger logger = Logger.getLogger(FeatureCalculations.class.getName());
    private final Map<String, FeatureCalculation> calculations;
    protected final FeatureAccess access = new FeatureAccess();
    private final boolean suppressErrors;

    public FeatureCalculations(boolean suppressErrors, Map<String, FeatureCalculation> calculations) {
        this.calculations = calculations;
        this.suppressErrors = suppressErrors;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        access.updateHeader(sample.getHeader());
        access.setSample(sample);

        String newFields[] = new String[calculations.size()];
        double newValues[] = new double[newFields.length];
        int i = 0;
        for (Map.Entry<String, FeatureCalculation> entry : calculations.entrySet()) {
            double value;
            try {
                value = entry.getValue().calculate(access);
            } catch (IllegalArgumentException e) {
                if (!suppressErrors) {
                    logger.warning("Feature Calculation tried to access missing metric: " + e);
                }
                value = 0;
            }
            newFields[i] = entry.getKey();
            newValues[i] = value;
            i++;
        }
        sample = sample.extend(newFields, newValues);
        output.writeSample(sample);
    }

    public interface FeatureCalculation {

        double calculate(FeatureAccess access);
    }
}

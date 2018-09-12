package bitflow4j.main.registry;

import bitflow4j.steps.PipelineStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * AnalsisRegistration defines an analysis step, a function to apply it on a pipeline and the options to that function.
 * The Builder allows for a fluent API.
 * Example:
 * <code>
 * AnalysisRegistrationBuilder.build("count", (params, pipeline) -> pipeline.step(new SampleCounter(params))
 * .withOptionalParameter("startCount")
 * .supportBatch();
 *
 * </code>
 */
public class AnalysisRegistration {
    private boolean supportsBatchProcessing;
    private boolean supportsStreamProcessing;
    private String name;
    private List<String> optionalParameters;
    private List<String> requiredParameters;
    private StepConstructor stepConstructor;

    public static Builder builder(String name, StepConstructor stepConstructor) {
        return new Builder(name, stepConstructor);
    }

    public String getName() {
        return name;
    }

    public StepConstructor getStepConstructor() {
        return stepConstructor;
    }

    public boolean isSupportsBatchProcessing() {
        return supportsBatchProcessing;
    }

    public boolean isSupportsStreamProcessing() {
        return supportsStreamProcessing;
    }

    public List<String> getOptionalParameters() {
        return optionalParameters;
    }

    public List<String> getRequiredParameters() {
        return requiredParameters;
    }

    /**
     * validateParameters takes a map of parameters and validates them against the specified optional and required parameters.
     * It returns a list of errors for unexpected or missing required parameters.
     *
     * @param params the input parameters to be validated
     * @return a list of errors in the specified input parameters (required but missing or unexpected)
     */
    public List<String> validateParameters(Map<String, String> params) {
        List<String> errors = new ArrayList<>();
        params.keySet().forEach(s -> {
            if (!optionalParameters.contains(s) && !requiredParameters.contains(s)) {
                errors.add("Unexpected parameter '" + s + "'");
            }
        });
        requiredParameters.forEach(s -> {
            if (!params.keySet().contains(s)) {
                errors.add("Missing required parameter '" + s + "'");
            }
        });
        return errors;
    }

    public String printCapability() {
        String batchSupport = "supports stream and batch";
        if (!supportsBatchProcessing) {
            batchSupport = "supports stream only";
        } else if (!supportsStreamProcessing) {
            batchSupport = "supports batch only";
        }

        return name + ":\trequired parameters: " + Arrays.toString(requiredParameters.toArray()) + ";\toptional parameter: " + Arrays.toString(optionalParameters.toArray()) + ";\t" + batchSupport;
    }

    public static class Builder {
        private boolean supportsBatchProcessing = false;
        private boolean supportsStreamProcessing = true;
        private String name;
        private List<String> optionalParameters;
        private List<String> requiredParameters;
        private StepConstructor stepConstructor;

        public Builder(String name, StepConstructor stepConstructor) {
            this.name = name;
            this.stepConstructor = stepConstructor;
        }

        public Builder supportBatch() {
            this.supportsBatchProcessing = true;
            return this;
        }

        public Builder enforceBatch() {
            this.supportsStreamProcessing = false;
            return this;
        }

        public Builder withRequiredParameters(String... requiredParameters) {
            this.requiredParameters = Arrays.asList(requiredParameters);
            return this;
        }

        public Builder withOptionalParameters(String... optionalParameters) {
            this.optionalParameters = Arrays.asList(optionalParameters);
            return this;
        }

        public AnalysisRegistration build() {
            AnalysisRegistration a = new AnalysisRegistration();
            a.requiredParameters = this.requiredParameters;
            a.optionalParameters = this.optionalParameters;
            a.supportsStreamProcessing = this.supportsStreamProcessing;
            a.supportsBatchProcessing = this.supportsBatchProcessing;
            a.stepConstructor = this.stepConstructor;
            a.name = this.name;
            return a;
        }
    }
}

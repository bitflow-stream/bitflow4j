package bitflow4j.script.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Registration {

    private final String name;
    private final List<String> optionalParameters = new ArrayList<>();
    private final List<String> requiredParameters = new ArrayList<>();

    public Registration(String name) {
        this.name = name;
    }

    public Registration optional(String... parameters) {
        Collections.addAll(optionalParameters, parameters);
        return this;
    }

    public Registration required(String... parameters) {
        Collections.addAll(requiredParameters, parameters);
        return this;
    }

    public String getName() {
        return name;
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

}

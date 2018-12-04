package bitflow4j.script.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractRegisteredStep {

    public final String name;
    public final List<String> optionalParameters = new ArrayList<>();
    public final List<String> requiredParameters = new ArrayList<>();

    private boolean hasGeneric = false;

    public AbstractRegisteredStep(String name) {
        this.name = name;
    }

    public AbstractRegisteredStep optional(String... parameters) {
        Collections.addAll(optionalParameters, parameters);
        return this;
    }

    public AbstractRegisteredStep required(String... parameters) {
        Collections.addAll(requiredParameters, parameters);
        return this;
    }

    public void acceptGenericConstructor() {
        hasGeneric = true;
    }

    public boolean hasGenericConstructor() {
        return hasGeneric;
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
        if (hasGeneric) {
            return errors;
        }

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

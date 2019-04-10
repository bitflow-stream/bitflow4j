package bitflow4j.script.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractRegisteredStep {

    public final String name;
    public String conventionName;
    public final String description;
    public final List<String> optionalParameters = new ArrayList<>();
    public final List<String> requiredParameters = new ArrayList<>();

    private boolean hasGeneric = false;

    public AbstractRegisteredStep(String name, String description) {
        this.name = name;
        this.conventionName = splitCamelCase(name, "-");
        this.description = description;
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

    public String getConventionName() {
        return conventionName;
    }

    // Splits a camelCase string into a lowercase string with delimiters instead of Uppercase
    private String splitCamelCase(String camelCase, String delimiter){
        //Check for 'processingstep', 'batchstep' or 'step' (and any uppercase variants) at the end of the Class-name and remove them
        String lowerCase = camelCase.toLowerCase();
        int index_processingstep = lowerCase.indexOf("processingstep");
        int index_batchstep = lowerCase.indexOf("batchstep");
        int index_step = lowerCase.indexOf("step");
        //Found word at the end
        if (index_processingstep >= 0 && lowerCase.length() == index_processingstep + "processingstep".length()) {
            camelCase = camelCase.substring(0, index_processingstep);
        }
        else if (index_batchstep >= 0 && lowerCase.length() == index_batchstep + "batchstep".length()) {
            camelCase = camelCase.substring(0, index_batchstep);
        }
        else if (index_processingstep == -1 && index_batchstep == -1 && index_step >= 0
                && lowerCase.length() == index_step + "step".length()) {
            camelCase = camelCase.substring(0, index_step);
        }

        // Splits the string at uppercase letters
        String[] classCapitals = camelCase.split("(?=\\p{Upper})");
        ArrayList<String> classWords = new ArrayList<>();
        int counter = 0;
        for (int i = 0; i < classCapitals.length; i++) {
            //We are not at the end of the list & at least this and the next String only contain one capitalized letter
            if(i < classCapitals.length - 1 && classCapitals[i].length() == 1 && classCapitals[i + 1].length() == 1){
                if(classWords.size() <= counter) {
                    classWords.add(classCapitals[i] + classCapitals[i + 1]);
                    counter = i;
                }
                else {
                    classWords.set(counter, classWords.get(counter) + classCapitals[i + 1]);
                }
            }
            else {
                if(i < classCapitals.length - 1 && classCapitals[i].length() == 1 && classCapitals[i + 1].length() != 1){
                    counter++;
                    continue;
                }
                // Normal Words with forst letter capitalized can be simply added
                classWords.add(classCapitals[i]);
                counter++;
            }
        }

        String result = "";
        for (int i = 0; i < classWords.size(); i++) {
            result += classWords.get(i).toLowerCase();
            if (i < classWords.size() - 1){
                result += delimiter;
            }
        }
        return result;
    }

}

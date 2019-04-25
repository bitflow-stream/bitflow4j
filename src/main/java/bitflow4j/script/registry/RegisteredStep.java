package bitflow4j.script.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RegisteredStep<BuilderClass> {

    private static final String[] removedSuffixes = new String[]{"processingstep", "batchstep", "step"};
    private static final String processingStepDelimiter = "-";

    private final String stepName;
    private final String description;
    private final List<String> optionalParameters = new ArrayList<>();
    private final List<String> requiredParameters = new ArrayList<>();
    private boolean hasGeneric = false;

    public final BuilderClass builder;

    public RegisteredStep(String classOrStepName, String description, BuilderClass builder) {
        this.stepName = splitCamelCase(classOrStepName);
        this.description = description;
        this.builder = builder;
    }

    public RegisteredStep<BuilderClass> optional(String... parameters) {
        Collections.addAll(optionalParameters, parameters);
        return this;
    }

    public RegisteredStep<BuilderClass> required(String... parameters) {
        Collections.addAll(requiredParameters, parameters);
        return this;
    }

    public List<String> getOptionalParameters() {
        return optionalParameters;
    }

    public List<String> getRequiredParameters() {
        return requiredParameters;
    }

    public String getStepName() {
        return stepName;
    }

    public String getDescription() {
        return description;
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

    public static String splitCamelCase(String camelCase) {
        return splitCamelCase(camelCase, processingStepDelimiter);
    }

    /**
     * Splits a camelCase string into a lowercase string with delimiters instead of Uppercase
     */
    public static String splitCamelCase(String camelCase, String delimiter) {
        // Remove redundant suffixes at the end of the class name
        for (String suffix : removedSuffixes) {
            camelCase = removeSuffix(camelCase, suffix);
        }

        // Splits the string at uppercase letters
        String[] classCapitals = camelCase.split("(?=\\p{Upper})");
        ArrayList<String> classWords = new ArrayList<>();
        int counter = 0;
        int offset = 0;
        boolean summarizedCapitals = false;
        for (int i = 0; i < classCapitals.length; i++) {
            //We are not at the end of the list & at least this and the next String only contain one capitalized letter
            if (i < classCapitals.length - 1 && classCapitals[i].length() == 1 && classCapitals[i + 1].length() == 1) {
                if (classWords.size() <= counter) {
                    classWords.add(classCapitals[i] + classCapitals[i + 1]);
                    counter = i - offset;
                    summarizedCapitals = true;
                    offset++;
                } else {
                    classWords.set(counter, classWords.get(counter) + classCapitals[i + 1]);
                    summarizedCapitals = true;
                    offset++;
                }
            } else {
                // Not the end of the list and the current string is a capital while the next one is a word,
                // add only if it has not been added yet.
                if (i < classCapitals.length - 1 && classCapitals[i].length() == 1 && classCapitals[i + 1].length() != 1
                        && summarizedCapitals) {
                    counter++;
                    summarizedCapitals = false;
                    continue;
                }

                // If last letter is (not the first and) a single capitalized letter, it has already been added
                if (i != 0 && i == classCapitals.length - 1 && classCapitals[i].length() == 1 && summarizedCapitals)
                    continue;

                summarizedCapitals = false;

                // Normal Words with first letter capitalized can be simply added
                classWords.add(classCapitals[i]);
                counter++;
            }
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < classWords.size(); i++) {
            result.append(classWords.get(i).toLowerCase());
            if (i < classWords.size() - 1) {
                result.append(delimiter);
            }
        }
        return result.toString();
    }

    private static String removeSuffix(String name, String lowerCaseSuffix) {
        String lowerCase = name.toLowerCase();
        int index = lowerCase.indexOf(lowerCaseSuffix);
        if (index >= 0 && lowerCase.length() == index + lowerCaseSuffix.length()) {
            name = name.substring(0, index);
        }
        return name;
    }

}

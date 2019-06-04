package bitflow4j.script.registry;

import java.util.ArrayList;

public class RegisteredStep<BuilderClass> {

    private static final String[] removedSuffixes = new String[]{"processingstep", "batchstep", "step"};
    private static final String processingStepDelimiter = "-";

    private final String stepName;
    private final String description;
    public final RegisteredParameterList parameters;

    public final BuilderClass builder;

    public RegisteredStep(String classOrStepName, String description, RegisteredParameterList parameters, BuilderClass builder) {
        this.stepName = splitCamelCase(classOrStepName);
        this.description = description;
        this.builder = builder;
        this.parameters = parameters == null ? new RegisteredParameterList() : parameters;
    }

    public String getStepName() {
        return stepName;
    }

    public String getDescription() {
        return description;
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

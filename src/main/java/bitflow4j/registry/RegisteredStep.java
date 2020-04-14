package bitflow4j.registry;

import bitflow4j.ProcessingStep;
import com.google.common.collect.Lists;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RegisteredStep {

    private static final Logger logger = Logger.getLogger(RegisteredStep.class.getName());

    private static final String[] removedSuffixes = new String[]{"processingstep", "step"};
    private static final String processingStepDelimiter = "-";

    private final Class<?> cls;
    private final String stepName;
    private final String description;
    private final Constructor<?> constructor;
    public final RegisteredParameterList parameters;

    public RegisteredStep(Class<?> cls) {
        this.cls = cls;
        this.stepName = extractStepName(cls);
        this.description = getDescriptionField(cls);
        this.constructor = findConstructor(cls);
        this.parameters = new RegisteredParameterList(constructor);
    }

    public String toString() {
        return String.format("%s (%s)", stepName, description);
    }

    public String getStepName() {
        return stepName;
    }

    public String getDescription() {
        return description;
    }

    //
    // ==================== Class metadata ====================
    //

    public String getClassName() {
        return cls.getName();
    }

    public boolean hasConstructor() {
        return constructor != null;
    }

    public boolean isAbstract() {
        return (cls.getModifiers() & (Modifier.ABSTRACT | Modifier.INTERFACE)) != 0;
    }

    public boolean isValidProcessingStep() {
        return ProcessingStep.class.isAssignableFrom(cls);
    }

    public static String extractStepName(Class<?> cls) {
        StepName annotation = cls.getAnnotation(StepName.class);
        if (annotation != null) {
            return annotation.value();
        }
        return classNameToStepName(cls.getSimpleName());
    }

    private static String getDescriptionField(Class<?> cls) {
        Description annotation = cls.getAnnotation(Description.class);
        if (annotation != null) {
            return annotation.value();
        }
        return "";
    }

    private static boolean isConstructable(Constructor<?> constructor) {
        for (Parameter param : constructor.getParameters()) {
            if (!RegisteredParameter.isParseable(param.getParameterizedType()))
                return false;
        }
        return true;
    }

    private static Constructor<?> findConstructor(Class<?> cls) {
        List<Constructor<?>> constructors = Lists.newArrayList(cls.getConstructors());

        // If there is at least one annotated constructor, consider those constructors only.
        List<Constructor<?>> annotatedConstructors = constructors.stream().filter((c) ->
                c.getAnnotation(BitflowConstructor.class) != null).collect(Collectors.toList());
        boolean hasAnnotatedConstructors = !annotatedConstructors.isEmpty();
        if (hasAnnotatedConstructors) {
            if (annotatedConstructors.size() > 1) {
                logger.warning(String.format("Class %s has multiple constructors annotated with %s. Only one should be annotated for predictable results.",
                        cls.getName(), BitflowConstructor.class.getName()));
            }
            constructors = annotatedConstructors;
        }

        // Use only constructors, that take primitive values or specific collection types.
        int totalConstructors = constructors.size();
        constructors.removeIf(constructor -> !isConstructable(constructor));
        if (hasAnnotatedConstructors && totalConstructors != constructors.size()) {
            logger.warning(String.format("Class %s contains a constructor annotated with %s, that is not constructable automatically.",
                    cls, BitflowConstructor.class.getName()));
        }
        if (constructors.isEmpty()) {
            return null;
        } else if (constructors.size() == 1) {
            return constructors.get(0);
        }

        // When there are multiple potential constructors, use the one with the most parameters.
        logger.warning(String.format("Class %s has %s potential constructors - the one with the most parameters will be registered." +
                        " Use the %s annotation on one constructor for predictable results.",
                cls.getName(), constructors.size(), BitflowConstructor.class.getName()));
        return constructors.stream().
                max(Comparator.comparingInt(Constructor::getParameterCount)).
                orElse(null);
    }

    //
    // ==================== Object instantiation ====================
    //

    public ProcessingStep instantiate(Map<String, Object> parameters) throws ConstructionException {
        if (!isValidProcessingStep())
            throw new ConstructionException(stepName, "Class cannot be instantiated as processing step: " + cls.getName());

        Object instance = constructObject(parameters);
        if (instance instanceof ProcessingStep) {
            return (ProcessingStep) instance;
        } else {
            throw new ConstructionException(stepName, String.format("Constructor of class %s did not return instance of %s, but %s: %s",
                    cls.getName(), ProcessingStep.class.getName(), instance.getClass().getName(), instance));
        }
    }

    private Object constructObject(Map<String, Object> dirtyParameterValues) throws ConstructionException {
        if (dirtyParameterValues.size() == 0) {
            return invokeConstructor(constructor, new Object[0]);
        }

        // Make parameter names case insensitive
        Map<String, Object> parameterValues = new HashMap<>();
        dirtyParameterValues.forEach((key, val) -> parameterValues.put(key.toLowerCase(), val));

        Parameter[] constructorParams = constructor.getParameters();
        Object[] parsedValues = new Object[parameterValues.size()];
        for (int i = 0; i < constructorParams.length; i++) {
            Parameter param = constructorParams[i];
            parsedValues[i] = parameterValues.get(param.getName().toLowerCase());
        }
        return invokeConstructor(constructor, parsedValues);
    }

    private Object invokeConstructor(Constructor<?> c, Object[] parameters) throws ConstructionException {
        Throwable exc;
        try {
            return c.newInstance(parameters);
        } catch (InvocationTargetException e) {
            exc = e.getTargetException();
        } catch (Exception e) {
            exc = e;
        }
        throw new ConstructionException(stepName, "Failed to create '" + stepName + "' with parameters " + Arrays.toString(parameters), exc);
    }

    //
    // ==================== Helper methods ====================
    //

    public static String classNameToStepName(String className) {
        // Remove some special suffixes to reduce verbosity
        for (String suffix : removedSuffixes) {
            className = removeSuffix(className, suffix);
        }
        return splitCamelCase(className, processingStepDelimiter);
    }

    /**
     * Splits a camelCase string into a lowercase string with delimiters instead of Uppercase
     */
    public static String splitCamelCase(String camelCase, String delimiter) {
        // Splits the string at uppercase letters
        String[] classCapitals = camelCase.split("(?=\\p{Upper})");
        ArrayList<String> classWords = new ArrayList<>();
        int counter = 0;
        int offset = 0;
        boolean summarizedCapitals = false;
        for (int i = 0; i < classCapitals.length; i++) {
            // We are not at the end of the list & at least this and the next String only contain one capitalized letter
            if (i < classCapitals.length - 1 && classCapitals[i].length() == 1 && classCapitals[i + 1].length() == 1) {
                if (classWords.size() <= counter) {
                    classWords.add(classCapitals[i] + classCapitals[i + 1]);
                    counter = i - offset;
                } else {
                    classWords.set(counter, classWords.get(counter) + classCapitals[i + 1]);
                }
                summarizedCapitals = true;
                offset++;
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

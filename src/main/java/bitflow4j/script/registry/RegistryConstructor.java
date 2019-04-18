package bitflow4j.script.registry;

import bitflow4j.PipelineStep;
import bitflow4j.steps.BatchHandler;
import bitflow4j.steps.fork.ScriptableDistributor;
import com.google.common.collect.Lists;
import com.thoughtworks.paranamer.Paranamer;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.logging.Logger;

class RegistryConstructor implements ProcessingStepBuilder, ForkBuilder, BatchStepBuilder {

    private static final Logger logger = Logger.getLogger(RegistryConstructor.class.getName());

    private final Paranamer paranamer;
    private final String name;
    private final String description;
    private final Class<?> cls;
    private final List<Constructor> constructors;
    private final Constructor stringMapConstructor;

    RegistryConstructor(Class<?> cls, Paranamer paranamer) {
        this.cls = cls;
        this.paranamer = paranamer;
        this.name = RegisteredStep.splitCamelCase(cls.getSimpleName());
        this.description = getDescriptionField(cls);
        this.constructors = Lists.newArrayList(cls.getConstructors());
        filterBadConstructors();
        stringMapConstructor = getMapConstructor();
    }

    //
    // ==================== Class metadata ====================
    //

    private static String getDescriptionField(Class<?> cls) {
        Description annotation = cls.getAnnotation(Description.class);
        if (annotation != null) {
            return annotation.value();
        }
        return "";
    }

    private void filterBadConstructors() {
        constructors.removeIf(constructor -> !isConstructable(constructor));
    }

    private boolean hasConstructors() {
        return !constructors.isEmpty() || stringMapConstructor != null;
    }

    private Constructor getMapConstructor() {
        try {
            return cls.getConstructor(Map.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private boolean isConstructable(Constructor constructor) {
        String[] names = paranamer.lookupParameterNames(constructor, false);
        if (names == null) {
            logger.fine("No Paranamer information found for class: " + constructor.getDeclaringClass().getName());
            return false;
        }

        Parameter[] constructorParams = constructor.getParameters();
        for (Parameter param : constructorParams) {
            switch (param.getType().getSimpleName()) {
                case "String":
                case "Double":
                case "double":
                case "Long":
                case "long":
                case "Float":
                case "float":
                case "Integer":
                case "int":
                case "Boolean":
                case "boolean":
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    //
    // ==================== Building steps ====================
    //

    @Override
    public PipelineStep buildProcessingStep(Map<String, String> parameters) throws IOException {
        if (!isProcessingStep())
            throw new ConstructionException(name, "Class cannot be instantiated as processing step: " + cls.getName());

        Object instance = constructObject(parameters);
        if (instance instanceof ProcessingStepBuilder) {
            return ((ProcessingStepBuilder) instance).buildProcessingStep(parameters);
        } else if (instance instanceof PipelineStep) {
            return (PipelineStep) instance;
        } else {
            throw new ConstructionException(name, String.format("Constructor of class %s did not return instance of %s or %s, but %s: %s",
                    cls.getName(), ProcessingStepBuilder.class.getName(), PipelineStep.class.getName(), instance.getClass().getName(), instance));
        }
    }

    @Override
    public BatchHandler buildBatchStep(Map<String, String> parameters) throws IOException {
        if (!isBatchStep())
            throw new ConstructionException(name, "Class cannot be instantiated as batch processing step: " + cls.getName());

        Object instance = constructObject(parameters);
        if (instance instanceof BatchStepBuilder) {
            return ((BatchStepBuilder) instance).buildBatchStep(parameters);
        } else if (instance instanceof BatchHandler) {
            return (BatchHandler) instance;
        } else {
            throw new ConstructionException(name, String.format("Constructor of class %s did not return instance of %s or %s, but %s: %s",
                    cls.getName(), BatchStepBuilder.class.getName(), BatchHandler.class.getName(), instance.getClass().getName(), instance));
        }
    }

    @Override
    public ScriptableDistributor buildFork(Map<String, String> parameters) throws IOException {
        if (!isFork())
            throw new ConstructionException(name, "Class cannot be instantiated as fork: " + cls.getName());

        Object instance = constructObject(parameters);
        if (instance instanceof ForkBuilder) {
            return ((ForkBuilder) instance).buildFork(parameters);
        } else if (instance instanceof ScriptableDistributor) {
            return (ScriptableDistributor) instance;
        } else {
            throw new ConstructionException(name, String.format("Constructor of class %s did not return instance of %s or %s, but %s: %s",
                    cls.getName(), ForkBuilder.class.getName(), ScriptableDistributor.class.getName(), instance.getClass().getName(), instance));
        }
    }

    private boolean isProcessingStep() {
        return ProcessingStepBuilder.class.isAssignableFrom(cls) || PipelineStep.class.isAssignableFrom(cls);
    }

    private boolean isBatchStep() {
        return BatchStepBuilder.class.isAssignableFrom(cls) || BatchHandler.class.isAssignableFrom(cls);
    }

    private boolean isFork() {
        return ForkBuilder.class.isAssignableFrom(cls) || ScriptableDistributor.class.isAssignableFrom(cls);
    }

    //
    // ==================== Registration ====================
    //

    public boolean register(Registry registry) {
        if ((cls.getModifiers() & (Modifier.ABSTRACT | Modifier.INTERFACE)) != 0) {
            logger.fine("Class is abstract or interface, not registered: " + cls.getName());
            return false;
        }
        if (!hasConstructors()) {
            logger.fine("Class missing valid constructor, not registered: " + cls.getName());
            return false;
        }

        if (isFork()) {
            registry.registerFork(configureRegisteredStep(new RegisteredStep<>(name, description, this)));
        } else if (isBatchStep()) {
            registry.registerBatchStep(configureRegisteredStep(new RegisteredStep<>(name, description, this)));
        } else if (isProcessingStep()) {
            registry.registerStep(configureRegisteredStep(new RegisteredStep<>(name, description, this)));
        } else {
            logger.warning("Not registering class, because it does not implement/subclass any supported type: " + cls.getName());
            return false;
        }
        return true;
    }

    private <T> RegisteredStep<T> configureRegisteredStep(RegisteredStep<T> reg) {
        if (!constructors.isEmpty()) {
            String[] constructor0ParamNames = paranamer.lookupParameterNames(constructors.get(0), false);
            Set<String> optionalParams = new HashSet<>();
            Set<String> requiredParams = new HashSet<>(Arrays.asList(constructor0ParamNames));

            constructors.forEach(constructor -> {
                List<String> paramNames = Arrays.asList(paranamer.lookupParameterNames(constructor, false));

                // build optional parameters
                for (String paramName : paramNames) {
                    if (!requiredParams.contains(paramName)) {
                        optionalParams.add(paramName);
                    }
                }

                // clean requiredParams by moving extraneous parameters from requiredParams to optionalParams
                for (Iterator<String> i = requiredParams.iterator(); i.hasNext(); ) {
                    String element = i.next();
                    if (!paramNames.contains(element)) {
                        optionalParams.add(element);
                        i.remove();
                    }
                }
            });
            reg.optional(optionalParams.toArray(new String[0]));
            reg.required(requiredParams.toArray(new String[0]));
        }
        if (stringMapConstructor != null) {
            reg.acceptGenericConstructor();
        }
        return reg;
    }

    //
    // ==================== Object instantiation ====================
    //

    private Object constructObject(Map<String, String> parameters) throws ConstructionException {
        String inputParamStr = sortedConcatenation(parameters.keySet());
        for (Constructor constructor : constructors) {
            if (constructor.getParameters().length != parameters.size()) {
                continue;
            }
            String[] parameterNames = paranamer.lookupParameterNames(constructor, false);
            if (parameterNames == null) {
                throw new ConstructionException(name, "No Paranamer information found in class: " + cls.getName());
            }

            String constructorParamConc = sortedConcatenation(Arrays.asList(parameterNames));
            if (inputParamStr.equals(constructorParamConc)) {
                return invokeSpecializedConstructor(constructor, parameterNames, parameters);
            }
        }

        // If none of the specialized constructors match, use the generic map constructor
        if (stringMapConstructor != null) {
            return invokeConstructor(stringMapConstructor, new Object[]{parameters});
        }
        throw new ConstructionException(name, "No matching Constructor found for parameters " + parameters.toString());
    }

    private String sortedConcatenation(Collection<String> parameters) {
        if (parameters.size() == 0) {
            return "";
        }
        return parameters.stream().sorted().reduce((s, s2) -> s.toLowerCase() + "_" + s2.toLowerCase()).get();
    }

    private Object invokeSpecializedConstructor(Constructor<?> constructor, String[] parameterNames, Map<String, String> dirtyParameters) throws ConstructionException {
        Map<String, String> parameters = new HashMap<>();
        dirtyParameters.forEach((key, val) -> parameters.put(key.toLowerCase(), val));
        if (parameters.size() == 0) {
            return invokeConstructor(constructor, new Object[0]);
        }
        Parameter[] constructorParams = constructor.getParameters();
        Object[] invokeParameters = new Object[parameters.size()];
        for (int i = 0; i < constructorParams.length; i++) {
            Parameter param = constructorParams[i];
            String value = parameters.get(parameterNames[i].toLowerCase());
            switch (param.getType().getSimpleName()) {
                case "String":
                    invokeParameters[i] = value;
                    break;
                case "Double":
                case "double":
                    invokeParameters[i] = Double.parseDouble(value);
                    break;
                case "Float":
                case "float":
                    invokeParameters[i] = Float.parseFloat(value);
                    break;
                case "Integer":
                case "int":
                    invokeParameters[i] = Integer.parseInt(value);
                    break;
                case "Long":
                case "long":
                    invokeParameters[i] = Long.parseLong(value);
                    break;
                case "Boolean":
                case "boolean":
                    invokeParameters[i] = Boolean.parseBoolean(value);
                    break;
                default:
                    throw new IllegalArgumentException("Parameter Type not allowed for automatic construction. Please implement and register a custom ProcessingStepBuilder to accept parameters of type " + param.getType().getSimpleName());
            }
        }
        return invokeConstructor(constructor, invokeParameters);
    }

    private Object invokeConstructor(Constructor c, Object[] parameters) throws ConstructionException {
        Throwable exc = null;
        try {
            return c.newInstance(parameters);
        } catch (InvocationTargetException e) {
            exc = e.getTargetException();
        } catch (Exception e) {
            exc = e;
        }
        throw new ConstructionException(name, "Failed to create '" + name + "' with parameters " + Arrays.toString(parameters) + ": " + exc.getMessage(), exc);
    }

}

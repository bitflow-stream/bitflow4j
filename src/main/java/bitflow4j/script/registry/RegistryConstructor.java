package bitflow4j.script.registry;

import bitflow4j.Pipeline;
import bitflow4j.PipelineStep;
import bitflow4j.misc.Pair;
import bitflow4j.steps.BatchHandler;
import bitflow4j.steps.fork.Fork;
import bitflow4j.steps.fork.ScriptableDistributor;
import com.google.common.collect.Lists;
import com.thoughtworks.paranamer.Paranamer;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.logging.Logger;

class RegistryConstructor {

    private static final Logger logger = Logger.getLogger(RegistryConstructor.class.getName());

    private final Paranamer paranamer;
    private final String name;
    private final String description;
    private final Class<?> cls;
    private final List<Constructor> constructors;
    private final Constructor stringMapConstructor;
    private final boolean isBuilder;

    RegistryConstructor(Class<?> cls, Paranamer paranamer, boolean isBuilder) {
        this.isBuilder = isBuilder;
        this.cls = cls;
        this.paranamer = paranamer;
        this.name = cls.getSimpleName();
        this.description = getDescriptionField(cls);
        this.constructors = Lists.newArrayList(cls.getConstructors());
        filterBadConstructors();
        stringMapConstructor = getMapConstructor();
    }

    private void filterBadConstructors() {
        constructors.removeIf(constructor -> !isConstructable(constructor));
    }

    private Constructor getMapConstructor() {
        try {
            return cls.getConstructor(Map.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    boolean hasConstructors() {
        return !constructors.isEmpty() || stringMapConstructor != null;
    }

    String getName() {
        return name;
    }

    String getDescription() {
        return description;
    }

    public void buildStep(Pipeline pipeline, Map<String, String> parameters) throws ConstructionException {
        Object instance = constructObject(parameters);
        if (isBuilder) {
            checkInstance(instance, PipelineBuilder.class);
            try {
                ((PipelineBuilder) instance).buildPipeline(pipeline);
            } catch (IOException e) {
                throw new ConstructionException(name, String.format("PipelineBuilder %s (class %s) failed to build fork: %s",
                        instance, instance.getClass().getName(), e.getMessage()));
            }
        } else {
            checkInstance(instance, PipelineStep.class);
            pipeline.step((PipelineStep) instance);
        }
    }

    public void buildFork(Pipeline pipeline, Collection<Pair<String, ScriptableDistributor.PipelineBuilder>> subPipelines, Map<String, String> parameters) throws ConstructionException {
        Object instance = constructObject(parameters);
        ScriptableDistributor fork;
        if (isBuilder) {
            checkInstance(instance, ForkBuilder.class);
            ForkBuilder builder = (ForkBuilder) instance;
            try {
                fork = builder.buildFork();
            } catch (IOException e) {
                throw new ConstructionException(name, String.format("ForkBuilder %s (class %s) failed to build fork: %s",
                        instance, instance.getClass().getName(), e.getMessage()));
            }
        } else {
            checkInstance(instance, ScriptableDistributor.class);
            fork = (ScriptableDistributor) instance;
        }
        try {
            fork.setSubPipelines(subPipelines);
        } catch (IOException e) {
            throw new ConstructionException(name, e.getMessage());
        }
        pipeline.step(new Fork(fork));
    }

    public BatchHandler buildBatchStep(Map<String, String> parameters) throws ConstructionException {
        Object instance = constructObject(parameters);
        BatchHandler result;
        if (isBuilder) {
            throw new ConstructionException(getName(), "Builders cannot be registered for batch processing steps");
        } else {
            checkInstance(instance, BatchHandler.class);
            result = (BatchHandler) instance;
        }
        return result;
    }

    private void checkInstance(Object instance, Class expectedCls) throws ConstructionException {
        if (!expectedCls.isInstance(instance)) {
            throw new ConstructionException(name, String.format("Constructor of class %s did not return instance of %s, but %s (%s)",
                    cls.getName(), expectedCls.getName(), instance.getClass().getName(), instance));
        }
    }

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
                    throw new IllegalArgumentException("Parameter Type not allowed for automatic construction. Please implement and register a custom PipelineBuilder to accept parameters of type " + param.getType().getSimpleName());
            }
        }
        return invokeConstructor(constructor, invokeParameters);
    }

    private Object invokeConstructor(Constructor c, Object[] parameters) throws ConstructionException {
        try {
            return c.newInstance(parameters);
        } catch (Exception e) {
            throw new ConstructionException(name, "Failed to create '" + name + "' with parameters " + Arrays.toString(parameters) + ": " + e.getMessage());
        }
    }

    public RegisteredPipelineStep createAnalysisRegistration() {
        RegisteredPipelineStep result = new RegisteredPipelineStep(getName(), getDescription()) {
            @Override
            public void buildStep(Pipeline pipeline, Map<String, String> parameters) throws ConstructionException {
                RegistryConstructor.this.buildStep(pipeline, parameters);
            }
        };
        configureRegistration(result);
        return result;
    }

    public RegisteredFork createRegisteredFork() {
        RegisteredFork result = new RegisteredFork(getName(), getDescription()) {
            @Override
            public void buildFork(Pipeline pipeline, Collection<Pair<String, ScriptableDistributor.PipelineBuilder>> subPipelines, Map<String, String> parameters) throws ConstructionException {
                RegistryConstructor.this.buildFork(pipeline, subPipelines, parameters);
            }
        };
        configureRegistration(result);
        return result;
    }

    public RegisteredBatchStep createBatchRegistration() {
        RegisteredBatchStep result = new RegisteredBatchStep(getName(), getDescription()) {
            @Override
            public BatchHandler buildStep(Map<String, String> parameters) throws ConstructionException {
                return RegistryConstructor.this.buildBatchStep(parameters);
            }
        };
        configureRegistration(result);
        return result;
    }

    private void configureRegistration(AbstractRegisteredStep reg) {
        if (!constructors.isEmpty()) {
            String[] constr0ParamNames = paranamer.lookupParameterNames(constructors.get(0), false);
            Set<String> optionalParams = new HashSet<>();
            Set<String> requiredParams = new HashSet<>(Arrays.asList(constr0ParamNames));

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
    }

    private String getDescriptionField(Class<?> cls) {
        Description annotation = cls.getAnnotation(Description.class);
        if (annotation != null) {
            return annotation.value();
        }
        return "";
    }

}

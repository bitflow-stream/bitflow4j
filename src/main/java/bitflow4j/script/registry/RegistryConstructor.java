package bitflow4j.script.registry;

import bitflow4j.PipelineStep;
import bitflow4j.steps.BatchHandler;
import bitflow4j.steps.fork.ScriptableDistributor;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.logging.Logger;

class RegistryConstructor implements ProcessingStepBuilder, ForkBuilder, BatchStepBuilder {

    private static final Logger logger = Logger.getLogger(RegistryConstructor.class.getName());

    private final String name;
    private final String description;
    private final Class<?> cls;
    private final List<Constructor<?>> constructors;

    RegistryConstructor(Class<?> cls) {
        this.cls = cls;
        this.name = RegisteredStep.splitCamelCase(cls.getSimpleName());
        this.description = getDescriptionField(cls);
        this.constructors = Lists.newArrayList(cls.getConstructors());
        filterBadConstructors();
    }

    private void filterBadConstructors() {
        constructors.removeIf(constructor -> !isConstructable(constructor));
    }

    public String getClassName() {
        return cls.getName();
    }

    public boolean hasConstructors() {
        return !constructors.isEmpty();
    }

    public boolean isAbstract() {
        return (cls.getModifiers() & (Modifier.ABSTRACT | Modifier.INTERFACE)) != 0;
    }

    public RegisteredStep<ForkBuilder> makeRegisteredFork() {
        return new RegisteredStep<>(name, description, parameterList(), this);
    }

    public RegisteredStep<BatchStepBuilder> makeRegisteredBatchStep() {
        return new RegisteredStep<>(name, description, parameterList(), this);
    }

    public RegisteredStep<ProcessingStepBuilder> makeRegisteredStep() {
        return new RegisteredStep<>(name, description, parameterList(), this);
    }

    private RegisteredParameterList parameterList() {
        return new RegisteredParameterList(constructors);
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

    private static boolean isConstructable(Constructor<?> constructor) {
        for (Parameter param : constructor.getParameters()) {
            if (!RegisteredParameter.isParseable(param.getParameterizedType()))
                return false;
        }
        return true;
    }

    //
    // ==================== Building steps ====================
    //

    @Override
    public PipelineStep buildProcessingStep(Map<String, Object> parameters) throws IOException {
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
    public BatchHandler buildBatchStep(Map<String, Object> parameters) throws IOException {
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
    public ScriptableDistributor buildFork(Map<String, Object> parameters) throws IOException {
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

    public boolean isProcessingStep() {
        return ProcessingStepBuilder.class.isAssignableFrom(cls) || PipelineStep.class.isAssignableFrom(cls);
    }

    public boolean isBatchStep() {
        return BatchStepBuilder.class.isAssignableFrom(cls) || BatchHandler.class.isAssignableFrom(cls);
    }

    public boolean isFork() {
        return ForkBuilder.class.isAssignableFrom(cls) || ScriptableDistributor.class.isAssignableFrom(cls);
    }

    //
    // ==================== Object instantiation ====================
    //

    private Object constructObject(Map<String, Object> parameters) throws ConstructionException {
        String inputParamStr = sortedConcatenation(parameters.keySet());
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameters().length != parameters.size()) {
                continue;
            }
            String constructorParamConc = sortedConcatenation(RegisteredParameterList.getParameterNames(constructor));
            if (inputParamStr.equals(constructorParamConc)) {
                return invokeSpecializedConstructor(constructor, parameters);
            }
        }
        throw new ConstructionException(name, "No matching Constructor found for parameters " + parameters.toString());
    }

    private static String sortedConcatenation(Collection<String> parameters) {
        if (parameters.size() == 0) {
            return "";
        }
        return parameters.stream().sorted().reduce((s, s2) -> s.toLowerCase() + "_" + s2.toLowerCase()).get();
    }

    private Object invokeSpecializedConstructor(Constructor<?> constructor, Map<String, Object> dirtyParameterValues) throws ConstructionException {
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
        throw new ConstructionException(name, "Failed to create '" + name + "' with parameters " + Arrays.toString(parameters), exc);
    }


}

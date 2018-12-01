package bitflow4j.script.registry;

import bitflow4j.Pipeline;
import bitflow4j.PipelineStep;
import bitflow4j.misc.Pair;
import bitflow4j.steps.fork.ScriptableDistributor;
import com.google.common.collect.Lists;
import com.thoughtworks.paranamer.Paranamer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.logging.Logger;

class GenericStepConstructor implements StepConstructor, ForkConstructor {

    private static final Logger logger = Logger.getLogger(GenericStepConstructor.class.getName());

    private final Paranamer paranamer;
    private final String name;
    private final Class cls;
    private final List<Constructor> constructors;

    GenericStepConstructor(Class cls, Paranamer paranamer) {
        this(cls.getSimpleName(), cls, paranamer);
    }

    GenericStepConstructor(String name, Class cls, Paranamer paranamer) {
        this.paranamer = paranamer;
        this.name = name;
        this.cls = cls;
        this.constructors = Lists.newArrayList(cls.getConstructors());
        filterBadConstructors();
    }

    private void filterBadConstructors() {
        constructors.removeIf(constructor -> !isConstructable(constructor));
    }

    boolean hasConstructors() {
        return !constructors.isEmpty();
    }

    String getName() {
        return name;
    }

    @Override
    public PipelineStep constructPipelineStep(Map<String, String> parameters) throws StepConstructionException {
        Object instance = constructObject(parameters);
        if (!(instance instanceof PipelineStep)) {
            throw new StepConstructionException(name, String.format("Constructor of class %s did not return instance of PipelineStep, but %s (%s)",
                    cls, instance.getClass().getName(), instance));
        }
        return (PipelineStep) instance;
    }

    @Override
    public ScriptableDistributor constructForkStep(Collection<Pair<String, Pipeline>> subPipelines, Map<String, String> parameters) throws StepConstructionException {
        Object instance = constructObject(parameters);
        if (!(instance instanceof ScriptableDistributor)) {
            throw new StepConstructionException(name, String.format("Constructor of class %s did not return instance of ScriptableDistributor, but %s (%s)",
                    cls, instance.getClass().getName(), instance));
        }
        ScriptableDistributor fork = (ScriptableDistributor) instance;
        fork.setSubPipelines(subPipelines);
        return fork;
    }

    private Object constructObject(Map<String, String> parameters) throws StepConstructionException {
        String inputParamStr = sortedConcatenation(parameters.keySet());
        for (Constructor constructor : constructors) {
            if (constructor.getParameters().length != parameters.size()) {
                continue;
            }
            String[] parameterNames = paranamer.lookupParameterNames(constructor, false);
            if (parameterNames == null) {
                throw new StepConstructionException(name, "No Paranamer information found in class: " + cls.getName());
            }

            String constructorParamConc = sortedConcatenation(Arrays.asList(parameterNames));
            if (inputParamStr.equals(constructorParamConc)) {
                try {
                    return invokeConstructor(constructor, parameterNames, parameters);
                } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NumberFormatException e) {
                    throw new StepConstructionException(name, "Failed to create '" + name + "' with parameters " + parameters + ": " + e.getMessage());
                }
            }
        }
        throw new StepConstructionException(name, "No matching Constructor found for parameters " + parameters.toString());
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

    private Object invokeConstructor(Constructor<?> constructor, String[] parameterNames, Map<String, String> dirtyParameters) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Map<String, String> parameters = new HashMap<>();
        dirtyParameters.forEach((key, val) -> parameters.put(key.toLowerCase(), val));
        if (parameters.size() == 0) {
            return constructor.newInstance();
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
                    throw new IllegalArgumentException("Parameter Type not allowed for automatic construction. Please implement and register a custom StepConstructor to accept parameters of type " + param.getType().getSimpleName());
            }
        }
        return constructor.newInstance(invokeParameters);
    }

    public AnalysisRegistration createAnalysisRegistration() {
        AnalysisRegistration result = new AnalysisRegistration(getName(), this);
        configureRegistration(result);

        // TODO somehow find out whether BATCH processing is supported
        result.supportStream();

        return result;
    }

    public ForkRegistration createForkRegistration() {
        ForkRegistration result = new ForkRegistration(getName(), this);
        configureRegistration(result);
        return result;
    }

    private void configureRegistration(Registration reg) {
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

}

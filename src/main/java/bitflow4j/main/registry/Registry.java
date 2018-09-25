package bitflow4j.main.registry;

import bitflow4j.steps.AbstractPipelineStep;
import bitflow4j.steps.PipelineStep;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * The Registry provides methods to register Inputs, Outputs, Processors and Forks which can be used to build
 * a Bitflow Pipeline.
 */
public class Registry {
    private Paranamer paranamer = new BytecodeReadingParanamer();
    private Map<String, AnalysisRegistration> analysisRegistrationMap = new HashMap<>();
    private Map<Object, ForkRegistration> forkRegistrationMap = new HashMap<>();

    /**
     * scans specified packages automatically for SubTypes of AbstractPipelineStep and registers them according to the
     * available constructors.
     * Warning: If no prefix is provided, all packages in classpath will be scanned,
     * this can result in significant higher compile times! (during tests 5s instead of 20ms)
     *
     * @param scanPackagePrefixes varargs of package prefixes, null for anything
     */
    public void scanForPipelineSteps(String... scanPackagePrefixes) {
        if (scanPackagePrefixes == null || scanPackagePrefixes.length == 0) {
            _scanForPipelineSteps(null);
            return;
        }

        for (String packagePrefix : scanPackagePrefixes) {
            _scanForPipelineSteps(packagePrefix);
        }
    }

    /**
     * registerAnalysis takes a registration and stores it for retrieval by the pipeline builder.
     *
     * @param analysisRegistration
     */
    public void registerAnalysis(AnalysisRegistration analysisRegistration) {
        analysisRegistrationMap.put(analysisRegistration.getName().toLowerCase(), analysisRegistration);
    }


    /**
     * returns a registered Analysis by name or null if none found.
     *
     * @param analysisName the name of the analysis
     * @return the registered analysis or null
     */
    public AnalysisRegistration getAnalysisRegistration(String analysisName) {
        return analysisRegistrationMap.getOrDefault(analysisName.toLowerCase(), null);
    }

    /**
     * returns a registered Fork by name or null if none found.
     *
     * @param forkName the name of the fork
     * @return the registered Fork or null
     */
    public ForkRegistration getFork(String forkName) {
        return forkRegistrationMap.getOrDefault(forkName.toLowerCase(), null);
    }

    public Collection<AnalysisRegistration> getCapabilities() {
        return analysisRegistrationMap.values();
    }

    private void _scanForPipelineSteps(String scanPackagePrefix) {
        Reflections reflections = new Reflections(scanPackagePrefix);
        Set<Class<? extends AbstractPipelineStep>> classes = reflections.getSubTypesOf(AbstractPipelineStep.class);
        for (Class<? extends AbstractPipelineStep> impl : classes) {
            String stepName = impl.getSimpleName();
            Constructor[] constructors = impl.getConstructors();
            AnalysisRegistration.Builder builder = AnalysisRegistration.builder(stepName, new GenericConstructorStepConstructor(stepName, constructors));
            setRequiredAndOptionalParams(constructors, builder);
            registerAnalysis(builder.build());
        }
    }

    private void setRequiredAndOptionalParams(Constructor[] constructors, AnalysisRegistration.Builder builder) {
        String[] constr0ParamNames = paranamer.lookupParameterNames(constructors[0], false);
        Set<String> optionalParams = new HashSet<>();
        Set<String> requiredParams = new HashSet<>(Arrays.asList(constr0ParamNames));

        Arrays.stream(constructors).forEach(constructor -> {
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

        builder.withOptionalParameters(optionalParams.toArray(new String[0]));
        builder.withRequiredParameters(requiredParams.toArray(new String[0]));
        registerAnalysis(builder.build());
    }

    private AbstractPipelineStep invokeConstructor(Constructor<?> constructor, Map<String, String> dirtyParameters) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Map<String, String> parameters = new HashMap<>();
        dirtyParameters.forEach((key, val) -> parameters.put(key.toLowerCase(), val));
        if (parameters.size() == 0) {
            return (AbstractPipelineStep) constructor.newInstance(new Object[0]);
        }
        String[] parameterNames = paranamer.lookupParameterNames(constructor, false);
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
                case "Integer":
                case "int":
                    invokeParameters[i] = Integer.parseInt(value);
                    break;
                case "Boolean":
                case "boolean":
                    invokeParameters[i] = Boolean.parseBoolean(value);
                    break;
                case "Float":
                case "float":
                    throw new IllegalArgumentException("Parameter Type Float not allowed for automatic construction. Please use Double or implement and register a custom StepConstructor.");
                default:
                    throw new IllegalArgumentException("Parameter Type not allowed for automatic construction. Please implement and register a custom StepConstructor to accept parameters of type " + param.getType().getSimpleName());
            }
        }
        return (AbstractPipelineStep) constructor.newInstance(invokeParameters);
    }

    private Optional<String> buildSortedConcatenation(String[] parameters) {
        if (parameters.length == 0) {
            return Optional.of("_empty_");
        }
        return Arrays.stream(parameters).sorted().reduce((s, s2) -> s.toLowerCase() + s2.toLowerCase());
    }

    private Optional<String> buildSortedKeyConcatenation(Map<String, String> parameters) {
        if (parameters.size() == 0) {
            return Optional.of("_empty_");
        }
        return parameters.keySet().stream().sorted().reduce((s, s2) -> s.toLowerCase() + s2.toLowerCase());
    }

    private class GenericConstructorStepConstructor implements StepConstructor {
        private String name;
        private Constructor[] constructors;

        private GenericConstructorStepConstructor(String name, Constructor[] constructors) {
            this.name = name;
            this.constructors = constructors;
        }

        @Override
        public PipelineStep constructPipelineStep(Map<String, String> parameters) throws StepConstructionException {
            Optional<String> inputParamConc = buildSortedKeyConcatenation(parameters);
            for (Constructor<?> constructor : this.constructors) {
                if (constructor.getParameters().length != parameters.size()) {
                    continue;
                }
                String[] parameterNames = paranamer.lookupParameterNames(constructor, false);
                Optional<String> constructorParamConc = buildSortedConcatenation(parameterNames);
                if (inputParamConc.get().equals(constructorParamConc.get())) {
                    try {
                        return invokeConstructor(constructor, parameters);
                    } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                        throw new StepConstructionException(name, "Exception occurred when creating step with parameters " + parameters + "; Exception: " + e.getMessage());
                    }
                }
            }
            throw new StepConstructionException(name, "No matching Constructor found for parameters " + parameters.toString());
        }
    }
}

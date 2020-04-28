package bitflow4j.registry;

import bitflow4j.ProcessingStep;
import com.google.gson.Gson;
import org.reflections.Reflections;

import java.util.*;
import java.util.logging.Logger;

/**
 * The Registry provides methods to register Inputs, Outputs, Processors and Forks which can be used to build
 * a Bitflow Pipeline.
 */
public class Registry {

    private static final Logger logger = Logger.getLogger(Registry.class.getName());

    private final Map<String, RegisteredStep> registeredSteps = new HashMap<>();

    /**
     * scans specified packages automatically for SubTypes of AbstractPipelineStep and registers them according to the
     * available constructors.
     * Warning: If no prefix is provided, all packages in classpath will be scanned,
     * this can result in significant higher compile times! (during tests 5s instead of 20ms)
     *
     * @param scanPackagePrefixes varargs of package prefixes, null or empty to scan every package
     */
    public void scanForProcessingSteps(String... scanPackagePrefixes) {
        if (scanPackagePrefixes == null || scanPackagePrefixes.length == 0) {
            _scanForProcessingSteps(null);
            return;
        }

        for (String packagePrefix : scanPackagePrefixes) {
            _scanForProcessingSteps(packagePrefix);
        }
    }

    public ProcessingStep instantiateStep(String stepName, List<String> parametersList) throws ConstructionException {
        RegisteredStep step = getRegisteredStep(stepName);
        if (step == null) {
            return null;
        }
        Map<String, Object> rawParameters = parseRawParameters(parametersList);
        Map<String, Object> parameters = step.parameters.parseRawParameters(rawParameters);
        return step.instantiate(parameters);
    }

    public RegisteredStep getRegisteredStep(String stepName) {
        return registeredSteps.getOrDefault(stepName.toLowerCase(), null);
    }

    public Collection<RegisteredStep> getAllRegisteredSteps() {
        return registeredSteps.values();
    }

    private void checkAlreadyRegistered(Map<String, ?> map, RegisteredStep step) {
        if (map.containsKey(step.getStepName())) {
            // TODO allow accessing conflicting classes via their fully qualified name
            logger.warning("Processing step with name " + step.getStepName() + " already registered, ignoring repeated registration");
        }
    }

    private void _scanForProcessingSteps(String scanPackagePrefix) {
        logger.info("Scanning for processing steps in package " + scanPackagePrefix);
        Reflections reflections = new Reflections(scanPackagePrefix);
        reflections.getSubTypesOf(ProcessingStep.class).forEach(this::registerClass);
    }

    public boolean registerClass(Class<?> impl) {
        return register(new RegisteredStep(impl));
    }

    public boolean register(RegisteredStep step) {
        if (step.isAbstract()) {
            logger.fine("Class is abstract or interface, not registered: " + step.getClassName());
            return false;
        }
        if (!step.hasConstructor()) {
            logger.fine("Class missing valid constructor, not registered: " + step.getClassName());
            return false;
        }
        if (!step.isValidProcessingStep()) {
            logger.warning("Not registering class, because it does not implement/subclass any supported type: " + step.getClassName());
            return false;
        }

        checkAlreadyRegistered(registeredSteps, step);
        logger.fine("Registering processing step: " + step);
        registeredSteps.put(step.getStepName(), step);
        return true;
    }

    private static Map<String, Object> parseRawParameters(List<String> strings) {
        if (strings == null || strings.isEmpty())
            return Collections.emptyMap();
        Map<String, Object> map = new HashMap<>();
        for (String param : strings) {
            String[] keyVal = param.split("=", 2);
            if (keyVal.length != 2) {
                throw new IllegalArgumentException(String.format("Illegal parameter format: %s", param));
            }
            map.put(keyVal[0].strip(), keyVal[1].strip());
        }
        return map;
    }

    // =================================================================
    // ======= Printing and formatting of known processing steps =======
    // =================================================================

    private static final Comparator<RegisteredStep> stepComparator = (a1, a2) -> a1.getStepName().compareToIgnoreCase(a2.getStepName());

    public void outputCapabilities() {
        System.out.println("Processing Steps:");
        getAllRegisteredSteps().forEach(this::outputCapability);
    }

    private void outputCapability(RegisteredStep registeredPipelineStep) {
        System.out.println(" - " + registeredPipelineStep.getStepName());
        String desc = registeredPipelineStep.getDescription();
        if (desc != null && !desc.isEmpty())
            System.out.println("     Description: " + desc);
        if (!registeredPipelineStep.parameters.getParams().isEmpty())
            System.out.println("     Parameters: " + registeredPipelineStep.parameters.getParams());
    }

    public void outputJsonCapabilities() {
        System.out.println(new Gson().toJson(getAllRegisteredSteps()));
    }

    private void addJsonCapabilities(Map<String, Object> root, String name, Collection<? extends RegisteredStep> reg) {
        if (reg.isEmpty())
            return;
        Map<String, Object> capabilities = new HashMap<>();
        for (RegisteredStep step : reg) {
            capabilities.put(step.getStepName(), new JsonCapability(step));
        }
        root.put(name, capabilities);
    }

    private static final class JsonParameter {
        public final String name;
        public final String type;
        public final boolean required;
        public final String defaultValue;

        JsonParameter(String name, String type, boolean required, Object defaultValue) {
            this.name = name;
            this.type = type;
            this.required = required;
            this.defaultValue = defaultValue == null ? null : defaultValue.toString();
        }

        JsonParameter(RegisteredParameter param) {
            this(param.name, param.toString(), !param.isOptional, param.defaultValue);
        }
    }

    private static final class JsonCapability {
        public final String name;
        public final String description;
        public final List<JsonParameter> parameters = new ArrayList<>();

        private JsonCapability(RegisteredStep step) {
            this.name = step.getStepName();
            this.description = step.getDescription();
            step.parameters.getParams().forEach(p -> parameters.add(new JsonParameter(p)));
            parameters.sort(Comparator.comparing(o -> !o.required + o.name)); // Sort optional parameters first (lexicographically: "false" < "true")
        }
    }

}

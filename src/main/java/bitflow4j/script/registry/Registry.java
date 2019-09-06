package bitflow4j.script.registry;

import bitflow4j.PipelineStep;
import bitflow4j.steps.BatchHandler;
import bitflow4j.steps.fork.ScriptableDistributor;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The Registry provides methods to register Inputs, Outputs, Processors and Forks which can be used to build
 * a Bitflow Pipeline.
 */
public class Registry {

    private static final Logger logger = Logger.getLogger(Registry.class.getName());

    private Map<String, RegisteredStep<ProcessingStepBuilder>> registeredSteps = new HashMap<>();
    private Map<String, RegisteredStep<BatchStepBuilder>> registeredBatchSteps = new HashMap<>();
    private Map<String, RegisteredStep<ForkBuilder>> registeredForks = new HashMap<>();

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

    /**
     * registerStep takes a registration and stores it for retrieval by the pipeline builder.
     */
    public void registerStep(RegisteredStep<ProcessingStepBuilder> registeredStep) {
        checkAlreadyRegistered(registeredSteps, registeredStep, "Processing step");
        logger.fine("Registering processing step: " + registeredStep);
        registeredSteps.put(registeredStep.getStepName(), registeredStep);
    }

    /**
     * returns a registered Analysis by name or null if none found.
     */
    public RegisteredStep<ProcessingStepBuilder> getRegisteredStep(String stepName) {
        return registeredSteps.getOrDefault(stepName.toLowerCase(), null);
    }

    /**
     * registerStep takes a registration and stores it for retrieval by the pipeline builder.
     */
    public void registerFork(RegisteredStep<ForkBuilder> registeredFork) {
        checkAlreadyRegistered(registeredForks, registeredFork, "Fork");
        logger.fine("Registering fork: " + registeredFork);
        registeredForks.put(registeredFork.getStepName(), registeredFork);
    }

    /**
     * returns a registered Fork by name or null if none found.
     */
    public RegisteredStep<ForkBuilder> getRegisteredFork(String forkName) {
        return registeredForks.getOrDefault(forkName.toLowerCase(), null);
    }

    /**
     * registerBatchStep registers the given batch step under its name, so it can be retrieved later.
     */
    public void registerBatchStep(RegisteredStep<BatchStepBuilder> batchStep) {
        checkAlreadyRegistered(registeredBatchSteps, batchStep, "Batch step");
        logger.fine("Registering batch step: " + batchStep);
        registeredBatchSteps.put(batchStep.getStepName(), batchStep);
    }

    private void checkAlreadyRegistered(Map<String, ?> map, RegisteredStep step, String stepType) {
        if (map.containsKey(step.getStepName())) {
            // TODO allow accessing conflicting classes via their fully qualified name
            logger.warning(stepType + " with name " + step.getStepName() + " already registered, ignoring repeated registration");
        }
    }

    /**
     * returns a registered batch processing step by name or null if none found.
     */
    public RegisteredStep<BatchStepBuilder> getRegisteredBatchStep(String name) {
        return registeredBatchSteps.getOrDefault(name.toLowerCase(), null);
    }

    public Collection<RegisteredStep<ProcessingStepBuilder>> getStreamCapabilities() {
        return registeredSteps.values();
    }

    public Collection<RegisteredStep<BatchStepBuilder>> getBatchCapabilities() {
        return registeredBatchSteps.values();
    }

    public Collection<RegisteredStep<ForkBuilder>> getForkCapabilities() {
        return registeredForks.values();
    }

    public Collection<RegisteredStep> getAllCapabilities() {
        Collection<RegisteredStep> result = new ArrayList<>();
        result.addAll(getStreamCapabilities());
        result.addAll(getBatchCapabilities());
        result.addAll(getForkCapabilities());
        return result;
    }

    private void _scanForProcessingSteps(String scanPackagePrefix) {
        logger.info("Scanning for pipeline steps in package " + scanPackagePrefix);
        Reflections reflections = new Reflections(scanPackagePrefix);

        // Check implementations of steps that can be directly instantiated
        reflections.getSubTypesOf(PipelineStep.class).forEach(this::registerClass);
        reflections.getSubTypesOf(BatchHandler.class).forEach(this::registerClass);
        reflections.getSubTypesOf(ScriptableDistributor.class).forEach(this::registerClass);

        // Check for implementations of "builder" types that can create steps
        reflections.getSubTypesOf(ProcessingStepBuilder.class).forEach(this::registerClass);
        reflections.getSubTypesOf(ForkBuilder.class).forEach(this::registerClass);
        reflections.getSubTypesOf(BatchStepBuilder.class).forEach(this::registerClass);
    }

    public boolean registerClass(Class impl) {
        return register(new RegistryConstructor(impl));
    }

    public boolean register(RegistryConstructor constructor) {
        if (constructor.isAbstract()) {
            logger.fine("Class is abstract or interface, not registered: " + constructor.getClassName());
            return false;
        }
        if (!constructor.hasConstructor()) {
            logger.fine("Class missing valid constructor, not registered: " + constructor.getClassName());
            return false;
        }

        if (constructor.isFork()) {
            registerFork(constructor.makeRegisteredFork());
        } else if (constructor.isBatchStep()) {
            registerBatchStep(constructor.makeRegisteredBatchStep());
        } else if (constructor.isProcessingStep()) {
            registerStep(constructor.makeRegisteredStep());
        } else {
            logger.warning("Not registering class, because it does not implement/subclass any supported type: " + constructor.getClassName());
            return false;
        }
        return true;
    }

}

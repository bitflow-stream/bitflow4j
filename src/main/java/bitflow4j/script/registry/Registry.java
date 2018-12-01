package bitflow4j.script.registry;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.steps.fork.ScriptableDistributor;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Logger;

/**
 * The Registry provides methods to register Inputs, Outputs, Processors and Forks which can be used to build
 * a Bitflow Pipeline.
 */
public class Registry {

    private static final Logger logger = Logger.getLogger(Registry.class.getName());

    private Paranamer paranamer = new BytecodeReadingParanamer();
    private Map<String, AnalysisRegistration> analysisRegistrationMap = new HashMap<>();
    private Map<Object, ForkRegistration> forkRegistrationMap = new HashMap<>();

    // Store classes that did not have a fitting constructor
    private Set<Class<?>> unconstructableClasses = new HashSet<>();

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
     * registerAnalysis takes a registration and stores it for retrieval by the pipeline builder.
     */
    public void registerFork(ForkRegistration forkRegistration) {
        forkRegistrationMap.put(forkRegistration.getName().toLowerCase(), forkRegistration);
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

    public Set<Class<?>> getUnconstructableClasses() {
        return unconstructableClasses;
    }

    private void _scanForPipelineSteps(String scanPackagePrefix) {
        logger.info("Scanning for pipeline steps in package " + scanPackagePrefix);
        Reflections reflections = new Reflections(scanPackagePrefix);

        // Scan for regular steps
        Set<Class<? extends AbstractPipelineStep>> stepClasses = reflections.getSubTypesOf(AbstractPipelineStep.class);
        for (Class<? extends AbstractPipelineStep> impl : stepClasses) {
            registerClass(impl, false);
        }

        // Scan for fork steps
        Set<Class<? extends ScriptableDistributor>> forkClasses = reflections.getSubTypesOf(ScriptableDistributor.class);
        for (Class<? extends ScriptableDistributor> impl : forkClasses) {
            registerClass(impl, true);
        }
    }

    public boolean registerClass(Class impl, boolean isFork) {
        if ((impl.getModifiers() & Modifier.ABSTRACT) == 0) {
            GenericStepConstructor stepConstructor = new GenericStepConstructor(impl, paranamer);
            if (stepConstructor.hasConstructors()) {
                if (getAnalysisRegistration(stepConstructor.getName()) != null) {
                    // TODO allow accessing conflicting classes via their fully qualified name
                    logger.warning("Pipeline step with name " + stepConstructor.getName() + " already registered, not registering class: " + impl.getName());
                } else {
                    if (isFork) {
                        registerFork(stepConstructor.createForkRegistration());
                    } else {
                        registerAnalysis(stepConstructor.createAnalysisRegistration());
                    }
                    return true;
                }
            } else {
                logger.fine("Class missing simple constructor, not registered: " + impl.getName());
            }
        } else {
            logger.fine("Class is abstract, not registered: " + impl.getName());
        }
        unconstructableClasses.add(impl);
        return false;
    }

}

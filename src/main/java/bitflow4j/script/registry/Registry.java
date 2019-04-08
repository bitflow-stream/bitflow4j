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
    private Map<String, RegisteredPipelineStep> analysisRegistrationMap = new HashMap<>();
    private Map<Object, RegisteredFork> forkRegistrationMap = new HashMap<>();

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
    public void registerAnalysis(RegisteredPipelineStep registeredPipelineStep) {
        String conventionName = splitCamelCase(registeredPipelineStep.name, "-");
        analysisRegistrationMap.put(conventionName, registeredPipelineStep);
        analysisRegistrationMap.put(registeredPipelineStep.name.toLowerCase(), registeredPipelineStep);
    }

    /**
     * returns a registered Analysis by name or null if none found.
     *
     * @param analysisName the name of the analysis
     * @return the registered analysis or null
     */
    public RegisteredPipelineStep getAnalysisRegistration(String analysisName) {
        return analysisRegistrationMap.getOrDefault(analysisName.toLowerCase(), null);
    }

    /**
     * registerAnalysis takes a registration and stores it for retrieval by the pipeline builder.
     */
    public void registerFork(RegisteredFork registeredFork) {
        String conventionName = splitCamelCase(registeredFork.name, "-");
        forkRegistrationMap.put(conventionName, registeredFork);
        forkRegistrationMap.put(registeredFork.name.toLowerCase(), registeredFork);
    }

    /**
     * returns a registered Fork by name or null if none found.
     *
     * @param forkName the name of the fork
     * @return the registered Fork or null
     */
    public RegisteredFork getFork(String forkName) {
        return forkRegistrationMap.getOrDefault(forkName.toLowerCase(), null);
    }

    public Collection<RegisteredPipelineStep> getCapabilities() {
        return analysisRegistrationMap.values();
    }

    public Set<Class<?>> getUnconstructableClasses() {
        return unconstructableClasses;
    }

    private void _scanForPipelineSteps(String scanPackagePrefix) {
        logger.info("Scanning for pipeline steps in package " + scanPackagePrefix);
        Reflections reflections = new Reflections(scanPackagePrefix);

        reflections.getSubTypesOf(AbstractPipelineStep.class).forEach(c -> registerClass(c, false, false));
        reflections.getSubTypesOf(PipelineBuilder.class).forEach(c -> registerClass(c, false, true));
        reflections.getSubTypesOf(ScriptableDistributor.class).forEach(c -> registerClass(c, true, false));
        reflections.getSubTypesOf(ForkBuilder.class).forEach(c -> registerClass(c, true, true));
    }

    public boolean registerClass(Class impl, boolean isFork, boolean isBuilder) {
        if ((impl.getModifiers() & Modifier.ABSTRACT) == 0) {
            RegistryConstructor stepConstructor = new RegistryConstructor(impl, paranamer, isBuilder);
            if (stepConstructor.hasConstructors()) {
                if (getAnalysisRegistration(stepConstructor.getName()) != null) {
                    // TODO allow accessing conflicting classes via their fully qualified name
                    logger.warning("Pipeline step with name " + stepConstructor.getName() + " already registered, not registering class: " + impl.getName());
                } else {
                    if (isFork) {
                        registerFork(stepConstructor.createRegisteredFork());
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

    // Splits a camelCase string into a lowercase string with delimiters instead of Uppercase
    private String splitCamelCase(String camelCase, String delimiter){
        // Splits the string at uppercase letters
        String[] classCapitals = camelCase.split("(?=\\p{Upper})");
        ArrayList<String> classWords = new ArrayList<>();
        int counter = 0;
        for (int i = 0; i < classCapitals.length; i++) {
            if(classCapitals[i].toLowerCase().equals("step")) continue;

            //We are not at the end of the list & at least this and the next String only contain one capitalized letter
            if(i < classCapitals.length - 1 && classCapitals[i].length() == 1 && classCapitals[i + 1].length() == 1){
                if(classWords.size() <= counter) {
                    classWords.add(classCapitals[i] + classCapitals[i + 1]);
                    counter = i;
                }
                else {
                    classWords.set(counter, classWords.get(counter) + classCapitals[i + 1]);
                }
            }
            else {
                if(i < classCapitals.length - 1 && classCapitals[i].length() == 1 && classCapitals[i + 1].length() != 1){
                    counter++;
                    continue;
                }
                classWords.add(classCapitals[i]);
                counter++;
            }
        }

        String result = "";
        for (int i = 0; i < classWords.size(); i++) {
            result += classWords.get(i).toLowerCase();
            if (i < classWords.size() - 1){
                result += delimiter;
            }
        }
        return result;
    }

}

package bitflow4j.script.registry;

import bitflow4j.Pipeline;
import bitflow4j.PipelineStep;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ScanForPipelineStepTest {

    private Registry registry;
    private RegisteredStep<ProcessingStepBuilder> requiredParamsRegistration;
    private RegisteredStep<ProcessingStepBuilder> mixedParamsRegistration;

    public static final String SCRIPT_PACKAGE = "bitflow4j.script.registry";

    @Before
    public void prepare() {
        registry = new Registry();
        registry.scanForProcessingSteps(SCRIPT_PACKAGE);
        requiredParamsRegistration = registry.getRegisteredStep("required-param");
        assertNotNull(requiredParamsRegistration);
        mixedParamsRegistration = registry.getRegisteredStep("mixed-param");
        assertNotNull(mixedParamsRegistration);
    }

    @After
    public void tearDown() {
        registry = null;
        requiredParamsRegistration = null;
        mixedParamsRegistration = null;
    }

    // ##### Tests using class RequiredParamStep for testing required and optional params #######
    @Test
    public void givenStepConstructor_whenInvokeWithOptionalAndRequiredArgs_thenSetBothProperties() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("requiredArg", "requiredArgValue");
        params.put("optionalArg", "optionalArgValue");

        Pipeline pipe = new Pipeline();
        pipe.step(
                requiredParamsRegistration.builder.buildProcessingStep(
                        requiredParamsRegistration.parameters.parseRawParameters((params))));
        PipelineStep res = pipe.steps.get(0);

        assertTrue(res instanceof RequiredParamStep);
        RequiredParamStep createdStep = (RequiredParamStep) res;
        assertEquals("requiredArgValue", createdStep.requiredArg);
        assertEquals("optionalArgValue", createdStep.optionalArg);
    }

    @Test
    public void givenStepConstructor_whenInvokeWithRequiredArgsOnly_thenSetRequiredArgOnly() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("requiredArg", "requiredArgValue");

        Pipeline pipe = new Pipeline();
        pipe.step(
                requiredParamsRegistration.builder.buildProcessingStep(
                        requiredParamsRegistration.parameters.parseRawParameters(params)));
        PipelineStep res = pipe.steps.get(0);

        assertTrue(res instanceof RequiredParamStep);
        RequiredParamStep createdStep = (RequiredParamStep) res;
        assertEquals("requiredArgValue", createdStep.requiredArg);
        assertNull(createdStep.optionalArg);
    }

    @Test(expected = ConstructionException.class)
    public void givenStepConstructor_whenInvokeWithoutRequiredArgs_thenThrowException() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("optionalArg", "optionalArgValue");

        try {
            Pipeline pipe = new Pipeline();
            // The parameters are not parsed in this test.
            pipe.step(requiredParamsRegistration.builder.buildProcessingStep(params));
        } catch (ConstructionException e) {
            assertEquals("required-param", e.getStepName());
            assertTrue(e.getMessage().contains("No matching Constructor found for parameters "));
            throw e;
        }
    }

    // ##### Tests using MixedParamStep for testing different parameter types and mixed constructors #######
    private MixedParamStep executeMixedStepConstruction(String parameterName, String parameterValue) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put(parameterName, parameterValue);
        return executeMixedStepConstruction(params);
    }

    private MixedParamStep executeMixedStepConstruction(Map<String, Object> params) throws IOException {
        Pipeline pipe = new Pipeline();
        pipe.step(mixedParamsRegistration.builder.buildProcessingStep(
                mixedParamsRegistration.parameters.parseRawParameters(params)));
        PipelineStep res = pipe.steps.get(0);
        assertTrue(res instanceof MixedParamStep);
        return (MixedParamStep) res;
    }

    @Test
    public void testMixedStep_withStringArg() throws IOException {
        MixedParamStep res = executeMixedStepConstruction("stringArg", "value");
        assertEquals("value", res.stringArg);
    }

    @Test
    public void testMixedStep_withIntArg() throws IOException {
        MixedParamStep res = executeMixedStepConstruction("intArg", "5");
        assertEquals(5, res.intArg);
    }

    @Test
    public void testMixedStep_withDoubleArg() throws IOException {
        MixedParamStep res = executeMixedStepConstruction("doubleArg", "5.5");
        assertEquals(5.5, res.doubleArg, 0);
    }

    @Test
    public void testMixedStep_withBooleanArg() throws IOException {
        MixedParamStep res = executeMixedStepConstruction("booleanArg", "true");
        assertTrue(res.booleanArg);
    }

    @Test
    public void testMixedStep_withoutArg() throws IOException {
        MixedParamStep res = executeMixedStepConstruction(new HashMap<>());
        assertNotNull(res);
        assertTrue(res.emptyConstructorCalled);
    }

    @Test
    public void testMixedStep_withAllArgs() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("stringArg", "value");
        params.put("intArg", "5");
        params.put("doubleArg", "5.5");
        params.put("booleanArg", "TRUE");

        MixedParamStep res = executeMixedStepConstruction(params);
        assertTrue(res.booleanArg);
    }

    @Test
    public void testMixedStep_withSomeArgs() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("doubleArg", "5.5");
        params.put("booleanArg", "TRUE");

        MixedParamStep res = executeMixedStepConstruction(params);
        assertTrue(res.booleanArg);
    }

    @Test(expected = ConstructionException.class)
    public void testMixedStep_withNonexistentCombination_shouldThrowException() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("stringArg", "value");
        params.put("intArg", "5");
        params.put("booleanArg", "TRUE");

        executeMixedStepConstruction(params);
    }
}
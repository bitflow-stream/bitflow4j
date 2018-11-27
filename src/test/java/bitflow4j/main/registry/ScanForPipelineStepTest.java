package bitflow4j.main.registry;

import bitflow4j.PipelineStep;
import bitflow4j.script.registry.AnalysisRegistration;
import bitflow4j.script.registry.Registry;
import bitflow4j.script.registry.StepConstructionException;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;


public class ScanForPipelineStepTest {

    // ##### Tests using class RequiredParamStep for testing required and optional params #######
    @Test
    public void givenStepConstructor_whenInvokeWithOptionalAndRequiredArgs_thenSetBothProperties() throws StepConstructionException {
        Registry registry = new Registry();
        registry.scanForPipelineSteps("bitflow4j.main.registry");
        AnalysisRegistration analysisRegistration = registry.getAnalysisRegistration("RequiredParamStep");
        Map<String, String> params = new HashMap<>();
        params.put("requiredArg", "requiredArgValue");
        params.put("optionalArg", "optionalArgValue");

        PipelineStep res = analysisRegistration.getStepConstructor().constructPipelineStep(params);

        assertTrue(res instanceof RequiredParamStep);
        RequiredParamStep createdStep = (RequiredParamStep) res;
        assertEquals("requiredArgValue", createdStep.requiredArg);
        assertEquals("optionalArgValue", createdStep.optionalArg);
    }

    @Test
    public void givenStepConstructor_whenInvokeWithRequiredArgsOnly_thenSetRequiredArgOnly() throws StepConstructionException {
        Registry registry = new Registry();
        registry.scanForPipelineSteps("bitflow4j.main.registry");
        AnalysisRegistration analysisRegistration = registry.getAnalysisRegistration("RequiredParamStep");
        Map<String, String> params = new HashMap<>();
        params.put("requiredArg", "requiredArgValue");

        PipelineStep res = analysisRegistration.getStepConstructor().constructPipelineStep(params);

        assertTrue(res instanceof RequiredParamStep);
        RequiredParamStep createdStep = (RequiredParamStep) res;
        assertEquals("requiredArgValue", createdStep.requiredArg);
        assertNull(createdStep.optionalArg);
    }

    @Test(expected = StepConstructionException.class)
    public void givenStepConstructor_whenInvokeWithoutRequiredArgs_thenThrowException() throws StepConstructionException {
        Registry registry = new Registry();
        registry.scanForPipelineSteps("bitflow4j.main.registry");
        AnalysisRegistration analysisRegistration = registry.getAnalysisRegistration("RequiredParamStep");
        Map<String, String> params = new HashMap<>();
        params.put("optionalArg", "optionalArgValue");

        try {
            PipelineStep res = analysisRegistration.getStepConstructor().constructPipelineStep(params);
        } catch (StepConstructionException e) {
            assertEquals("RequiredParamStep", e.getStepName());
            assertTrue(e.getMessage().contains("No matching Constructor found for parameters "));
            throw e;
        }
    }

    // ##### Tests using MixedParamStep for testing different parameter types and mixed constructors #######
    private MixedParamStep executeMixedStepConstruction(String parameterName, String parameterValue) throws StepConstructionException {
        Map<String, String> params = new HashMap<>();
        params.put(parameterName, parameterValue);
        return executeMixedStepConstruction(params);
    }

    private MixedParamStep executeMixedStepConstruction(Map<String, String> params) throws StepConstructionException {
        Registry registry = new Registry();
        registry.scanForPipelineSteps("bitflow4j.main.registry");
        AnalysisRegistration analysisRegistration = registry.getAnalysisRegistration("MixedParamStep");

        PipelineStep res = analysisRegistration.getStepConstructor().constructPipelineStep(params);

        assertTrue(res instanceof MixedParamStep);
        return (MixedParamStep) res;
    }


    @Test
    public void testMixedStep_withStringArg() throws StepConstructionException {
        MixedParamStep res = executeMixedStepConstruction("stringArg", "value");
        assertEquals("value", res.stringArg);
    }

    @Test
    public void testMixedStep_withIntArg() throws StepConstructionException {
        MixedParamStep res = executeMixedStepConstruction("intArg", "5");
        assertEquals(5, res.intArg);
    }

    @Test
    public void testMixedStep_withDoubleArg() throws StepConstructionException {
        MixedParamStep res = executeMixedStepConstruction("doubleArg", "5.5");
        assertEquals(5.5, res.doubleArg, 0);
    }

    @Test
    public void testMixedStep_withBooleanArg() throws StepConstructionException {
        MixedParamStep res = executeMixedStepConstruction("booleanArg", "true");
        assertTrue(res.booleanArg);
    }

    @Test
    public void testMixedStep_withoutArg() throws StepConstructionException {
        MixedParamStep res = executeMixedStepConstruction(new HashMap<>());
        assertNotNull(res);
        assertTrue(res.emptyConstructorCalled);
    }

    @Test
    public void testMixedStep_withAllArgs() throws StepConstructionException {
        Map<String, String> params = new HashMap<>();
        params.put("stringArg", "value");
        params.put("intArg", "5");
        params.put("doubleArg", "5.5");
        params.put("booleanArg", "TRUE");

        MixedParamStep res = executeMixedStepConstruction(params);
        assertTrue(res.booleanArg);
    }

    @Test
    public void testMixedStep_withSomeArgs() throws StepConstructionException {
        Map<String, String> params = new HashMap<>();
        params.put("doubleArg", "5.5");
        params.put("booleanArg", "TRUE");

        MixedParamStep res = executeMixedStepConstruction(params);
        assertTrue(res.booleanArg);
    }

    @Test(expected = StepConstructionException.class)
    public void testMixedStep_withNonexistentCombination_shouldThrowException() throws StepConstructionException {
        Map<String, String> params = new HashMap<>();
        params.put("stringArg", "value");
        params.put("intArg", "5");
        params.put("booleanArg", "TRUE");

        executeMixedStepConstruction(params);
    }
}
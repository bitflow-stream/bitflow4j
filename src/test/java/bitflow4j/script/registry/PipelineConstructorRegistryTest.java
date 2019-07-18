package bitflow4j.script.registry;

import bitflow4j.Pipeline;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.*;

public class PipelineConstructorRegistryTest {

    private Registry registry;

    public static final String SCRIPT_PACKAGE = "bitflow4j.script.registry";

    public PipelineConstructorRegistryTest() {
        registry = new Registry();
        registry.scanForProcessingSteps(SCRIPT_PACKAGE);
    }

    @SuppressWarnings("unchecked")
    private <T> T constructTestStep(String stepName, Map<String, Object> params) throws IOException {
        RegisteredStep<ProcessingStepBuilder> registration = registry.getRegisteredStep(stepName);
        assertNotNull(registration);
        Pipeline pipe = new Pipeline();
        return (T) registration.builder.buildProcessingStep(
                registration.parameters.parseRawParameters(params));
    }

    private void testFailedConstruction(String stepName, String errorMessage, Map<String, Object> params) {
        Throwable e = assertThrows(IllegalArgumentException.class, () -> constructTestStep(stepName, params));
        assertThat(e.getMessage(), containsString(errorMessage));
    }

    @Test
    public void testNormalStepConstruction() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("stringArg", "value");
        params.put("intArg", "5");
        params.put("doubleArg", "5.5");
        params.put("booleanArg", "TRUE");
        params.put("boolList", Arrays.asList("true", "false", "true"));
        params.put("intArray", Arrays.asList("2", "4", "6"));
        params.put("stringArray", Arrays.asList("x", "1111"));

        Map<String, String> inputFloatMap = new HashMap<>();
        inputFloatMap.put("hello", "1.0001");
        inputFloatMap.put("2", "4.005");
        params.put("floatMap", inputFloatMap);

        TestParamsStep res = constructTestStep("test-params", params);

        assertEquals("value", res.stringArg);
        assertEquals(5, res.intArg);
        assertEquals(5.5, res.doubleArg, 0.000000000001);
        assertTrue(res.booleanArg);
        assertArrayEquals(new Boolean[]{true, false, true}, res.boolList.toArray(Boolean[]::new));
        assertArrayEquals(new int[]{2, 4, 6}, res.intArray);
        assertArrayEquals(new String[]{"x", "1111"}, res.stringArray);

        HashMap<String, Float> expectedMap = new HashMap<>();
        expectedMap.put("hello", 1.0001F);
        expectedMap.put("2", 4.005F);

        // Use TreeMap for printing, to get a sorted and predictable output
        // TODO find way to assert equal maps
        String expectedMapString = new TreeMap<>(expectedMap).toString();
        String actualMapString = new TreeMap<>(res.floatMap).toString();

        assertEquals(expectedMapString, actualMapString);
    }

    @Test
    public void testEmptyConstructorDoesNotWork() throws IOException {
        testFailedConstruction("test-params", "Missing required parameter", new HashMap<>());
    }

    @Test
    public void testOtherConstructorDoesNotWork() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("doubleArg", "5.5");
        params.put("booleanArg", "TRUE");
        testFailedConstruction("test-params", "Missing required parameter", params);
    }

    @Test
    public void testMissingParameters() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("doubleArg", "5.5");
        params.put("booleanArg", "TRUE");
        params.put("intArg", "5");
        testFailedConstruction("test-params", "Missing required parameter", params);
    }

    @Test
    public void testMissingAndUnsupportedParameters() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("doubleArg", "5.5");
        params.put("booleanArg", "TRUE");
        params.put("intArg", "5");
        params.put("HELLO", "xxx");
        params.put("WRONG", "tyy");
        testFailedConstruction("test-params", "Missing required parameter", params);
    }

    @Test
    public void testUnsupportedParameters() throws IOException {
        Map<String, Object> params = new HashMap<>();

        // Add all required parameters
        params.put("stringArg", "value");
        params.put("intArg", "5");
        params.put("doubleArg", "5.5");
        params.put("booleanArg", "TRUE");
        params.put("boolList", Arrays.asList("true", "false", "true"));
        params.put("intArray", Arrays.asList("2", "4", "6"));
        params.put("stringArray", Arrays.asList("x", "1111"));
        Map<String, String> inputFloatMap = new HashMap<>();
        inputFloatMap.put("hello", "1.0001");
        inputFloatMap.put("2", "4.005");
        params.put("floatMap", inputFloatMap);

        // Also add wrong parameter
        params.put("HELLO", "5.5");

        testFailedConstruction("test-params", "Unknown parameter: HELLO", params);
    }

    @Test
    public void testUseMarkedConstructor() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("intArg", "5");
        TestParamsMarkedConstructorStep step = constructTestStep("test-params-marked-constructor", params);
        assertEquals(step.intArg, 5);
        assertNull(step.stringArg);
    }

    @Test
    public void testUnmarkedConstructorDoesNotWork() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("intArg", "5");
        params.put("doubleArg", "5.5");
        testFailedConstruction("test-params-marked-constructor", "Unknown parameter: doubleArg", params);
    }

    @Test
    public void testSingleConstructor() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("intArg", "5");
        TestParamsSingleConstructorStep step = constructTestStep("test-params-single-constructor", params);
        assertEquals(step.intArg, 5);
        assertNull(step.stringArg);
    }

    @Test
    public void testOptional_allParameters() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("requiredArg", "567");
        params.put("stringArg", "value");
        params.put("intArg", "5");
        params.put("boolArg", "FALSE");
        params.put("longArg", "100");
        params.put("doubleArg", "100.5");
        params.put("floatArg", "200.5");

        TestOptionalParamsStep step = constructTestStep("test-optional-params", params);

        assertEquals(567, step.requiredVal);
        assertEquals("value", step.stringVal);
        assertEquals(5, step.intVal);
        assertFalse(step.boolVal);
        assertEquals(100, step.longVal);
        assertEquals(100.5, step.doubleVal, 0);
        assertEquals(200.5, step.floatVal, 0);
    }

    @Test
    public void testOptional_someMissingParameters() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("requiredArg", "567");
        params.put("intArg", "5");
        params.put("boolArg", "FALSE");
        params.put("floatArg", "200.5");

        TestOptionalParamsStep step = constructTestStep("test-optional-params", params);

        assertEquals(567, step.requiredVal);
        assertEquals(5, step.intVal);
        assertFalse(step.boolVal);
        assertEquals(200.5, step.floatVal, 0);

        // Filled with default values
        assertEquals("hello", step.stringVal);
        assertEquals(44.44, step.doubleVal, 0);
        assertEquals(33, step.longVal);
    }

    @Test
    public void testOptional_onlyRequiredParameter() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("requiredArg", "567");

        TestOptionalParamsStep step = constructTestStep("test-optional-params", params);

        assertEquals(567, step.requiredVal);

        // Filled with default values
        assertEquals(22, step.intVal);
        assertTrue(step.boolVal);
        assertEquals(10, step.floatVal, 0);
        assertEquals("hello", step.stringVal);
        assertEquals(44.44, step.doubleVal, 0);
        assertEquals(33, step.longVal);
    }

    @Test
    public void testOptional_missingRequiredParameter() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("intArg", "5");
        params.put("boolArg", "FALSE");
        params.put("floatArg", "200.5");
        testFailedConstruction("test-optional-params", "Missing required parameter requiredArg", params);
    }

    @Test
    public void testOptional_noParameters() throws IOException {
        testFailedConstruction("test-optional-params", "Missing required parameter requiredArg", new HashMap<>());
    }

    @Test
    public void testOptional_unsupportedParameters() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("requiredArg", "5");
        params.put("HELLO", "200.5");
        testFailedConstruction("test-optional-params", "Unknown parameter: HELLO", params);
    }

}

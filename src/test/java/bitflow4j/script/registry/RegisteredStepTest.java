package bitflow4j.script.registry;

import bitflow4j.NoopStep;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RegisteredStepTest {

    private final RegisteredStep<ProcessingStepBuilder> sampleRegisteredStep = new RegisteredStep<ProcessingStepBuilder>("test",
            "This is the description.", parameters -> new NoopStep())
            .required("require_me").optional("optional_me");

    @Test
    public void givenParamsWithMissingRequiredParam_whenValidateParameters_thenReturnErrorMessage() {
        Map<String, String> missingRequiredParam = new HashMap<>();

        List<String> errs = sampleRegisteredStep.validateParameters(missingRequiredParam);

        assertEquals(1, errs.size());
        assertEquals("Missing required parameter 'require_me'", errs.get(0));
    }

    @Test
    public void givenParamsWithExtranousParam_whenValidateParameters_thenReturnErrorMessage() {
        Map<String, String> missingRequiredParam = new HashMap<>();
        missingRequiredParam.put("require_me", "sample_value");
        missingRequiredParam.put("extraneous_param", "not_allowed");

        List<String> errs = sampleRegisteredStep.validateParameters(missingRequiredParam);

        assertEquals(1, errs.size());
        assertEquals("Unexpected parameter 'extraneous_param'", errs.get(0));
    }

    @Test
    public void givenParamsWithOptionalAndRequiredParams_whenValidateParameters_thenReturnEmptyErrorList() {
        Map<String, String> missingRequiredParam = new HashMap<>();
        missingRequiredParam.put("require_me", "sample_value");
        missingRequiredParam.put("optional_me", "sample_value");

        List<String> errs = sampleRegisteredStep.validateParameters(missingRequiredParam);

        assertNotNull(errs);
        assertEquals(0, errs.size());
    }

}
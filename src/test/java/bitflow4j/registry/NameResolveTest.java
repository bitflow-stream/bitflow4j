package bitflow4j.registry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NameResolveTest {

    private void testExpectedResult(String input, String expectation) {
        assertEquals(expectation, RegisteredStep.classNameToStepName(input));
    }

    @Test
    public void testNameResolveSimpleCases() {
        testExpectedResult("Name", "name");
        testExpectedResult("SimpleName", "simple-name");
        testExpectedResult("Superlongestclassnameieversaw", "superlongestclassnameieversaw");
        testExpectedResult("N", "n");
        testExpectedResult("ALLBIG", "allbig");
    }

    @Test
    public void testNameResolveEdgeCases() {
        testExpectedResult("", "");
        testExpectedResult("KMeans", "k-means");
        testExpectedResult("KAMeans", "ka-means");
        testExpectedResult("SomeKMeans", "some-k-means");
        testExpectedResult("SomeKAMeans", "some-ka-means");
        testExpectedResult("SomeMeansK", "some-means-k");
        testExpectedResult("SomeMeansKA", "some-means-ka");
        testExpectedResult("FFTCalculator", "fft-calculator");
        testExpectedResult("CalculateFFT", "calculate-fft");
        testExpectedResult("BIGSmallBIGSmall", "big-small-big-small");
    }

    @Test
    public void testNameResolveStepStripping() {
        testExpectedResult("MyFancyStep", "my-fancy");
        testExpectedResult("MyFancyProcessingStep", "my-fancy");
        testExpectedResult("StepWiseCalculator", "step-wise-calculator");
        testExpectedResult("calculateBatchStepMinMax", "calculate-batch-step-min-max");
        testExpectedResult("BigPROCESSINGSTEP", "big");
        testExpectedResult("AStep", "a");
    }

}

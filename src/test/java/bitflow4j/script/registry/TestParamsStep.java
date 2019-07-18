package bitflow4j.script.registry;

import bitflow4j.AbstractPipelineStep;

import java.util.List;
import java.util.Map;

public class TestParamsStep extends AbstractPipelineStep {

    public boolean emptyConstructorCalled = false;

    String stringArg = null;
    int intArg;
    double doubleArg;
    Boolean booleanArg;
    List<Boolean> boolList;
    int[] intArray;
    Map<String, Float> floatMap;
    String[] stringArray;

    // Main constructor, should be picked by the registry. No optional parameters.
    public TestParamsStep(String stringArg, int intArg, double doubleArg, Boolean booleanArg,
                          List<Boolean> boolList, int[] intArray, String[] stringArray, Map<String, Float> floatMap) {
        this.stringArg = stringArg;
        this.intArg = intArg;
        this.doubleArg = doubleArg;
        this.booleanArg = booleanArg;
        this.boolList = boolList;
        this.intArray = intArray;
        this.stringArray = stringArray;
        this.floatMap = floatMap;
    }

    // Other constructors should be ignored
    public TestParamsStep(double doubleArg, Boolean booleanArg) {
        this.doubleArg = doubleArg;
        this.booleanArg = booleanArg;
    }

    public TestParamsStep() {
        emptyConstructorCalled = true;
    }

}

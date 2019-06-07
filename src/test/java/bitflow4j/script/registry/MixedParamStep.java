package bitflow4j.script.registry;

import bitflow4j.AbstractPipelineStep;

import java.util.List;
import java.util.Map;

/**
 * This is a sample Class to test constructor autodiscovery by argument.
 * It implements one constructor per argument type plus a mixed constructor and can be used to test.
 */
public class MixedParamStep extends AbstractPipelineStep {

    public boolean emptyConstructorCalled = false;

    String stringArg = null;
    int intArg;
    double doubleArg;
    Boolean booleanArg;
    List<Boolean> boolList;
    int[] intArray;
    Map<String, Float> floatMap;
    String[] stringArray;

    // #### mixed constructors ####
    // all
    public MixedParamStep(String stringArg, int intArg, double doubleArg, Boolean booleanArg, List<Boolean> boolList, int[] intArray, String[] stringArray, Map<String, Float> floatMap) {
        this.stringArg = stringArg;
        this.intArg = intArg;
        this.doubleArg = doubleArg;
        this.booleanArg = booleanArg;
        this.boolList = boolList;
        this.intArray = intArray;
        this.stringArray = stringArray;
        this.floatMap = floatMap;
    }

    // some
    public MixedParamStep(double doubleArg, Boolean booleanArg) {
        this.doubleArg = doubleArg;
        this.booleanArg = booleanArg;
    }

    // ###### none or single argument constructor ######
    public MixedParamStep() {
        emptyConstructorCalled = true;
    }

    public MixedParamStep(String stringArg) {
        this.stringArg = stringArg;
    }

    public MixedParamStep(int intArg) {
        this.intArg = intArg;
    }

    public MixedParamStep(double doubleArg) {
        this.doubleArg = doubleArg;
    }

    public MixedParamStep(Boolean booleanArg) {
        this.booleanArg = booleanArg;
    }

}

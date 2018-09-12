package bitflow4j.main.registry;

import bitflow4j.steps.AbstractPipelineStep;

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

    // #### mixed constructors ####
    // all
    public MixedParamStep(String stringArg, int intArg, double doubleArg, Boolean booleanArg) {
        this.stringArg = stringArg;
        this.intArg = intArg;
        this.doubleArg = doubleArg;
        this.booleanArg = booleanArg;
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

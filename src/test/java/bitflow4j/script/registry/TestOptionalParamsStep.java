package bitflow4j.script.registry;

import bitflow4j.AbstractPipelineStep;

public class TestOptionalParamsStep extends AbstractPipelineStep {

    public int requiredVal;

    public int intVal;
    public String stringVal;
    public boolean boolVal;
    public long longVal;
    public double doubleVal;
    public float floatVal;

    public TestOptionalParamsStep(int requiredArg,
                                  @Optional(defaultBool = true) boolean boolArg,
                                  @Optional(defaultLong = 33) long longArg,
                                  @Optional(defaultDouble = 44.44) double doubleArg,
                                  @Optional(defaultInt = 22) int intArg,
                                  @Optional(defaultString = "hello") String stringArg,
                                  @Optional(defaultFloat = 10) float floatArg) {
        this.requiredVal = requiredArg;

        this.boolVal = boolArg;
        this.longVal = longArg;
        this.doubleVal = doubleArg;
        this.intVal = intArg;
        this.stringVal = stringArg;
        this.floatVal = floatArg;
    }

    public TestOptionalParamsStep(int intVal) {
        // This should not be called, not even when only the intVal parameter is provided to the bitflow script.
        throw new UnsupportedOperationException();
    }

}

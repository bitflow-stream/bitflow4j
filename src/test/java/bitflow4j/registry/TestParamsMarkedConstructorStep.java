package bitflow4j.registry;

public class TestParamsMarkedConstructorStep extends TestParamsStep {

    @BitflowConstructor
    public TestParamsMarkedConstructorStep(int intArg) {
        super();
        this.intArg = intArg;
    }

    public TestParamsMarkedConstructorStep(int intArg, double doubleArg) {
        super();
        this.intArg = intArg;
        this.doubleArg = doubleArg;
    }

}

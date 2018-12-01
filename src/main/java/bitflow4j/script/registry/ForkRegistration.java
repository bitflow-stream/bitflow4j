package bitflow4j.script.registry;

/**
 * ForkRegistration Meta information about a fork and a method to generate it from parameters.
 */
public class ForkRegistration extends Registration {

    private final ForkConstructor forkConstructor;

    public ForkRegistration(String name, ForkConstructor forkConstructor) {
        super(name);
        this.forkConstructor = forkConstructor;
    }

    public ForkConstructor getForkConstructor() {
        return forkConstructor;
    }

}

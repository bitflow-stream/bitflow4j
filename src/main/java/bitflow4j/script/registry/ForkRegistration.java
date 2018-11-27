package bitflow4j.script.registry;

/**
 * ForkRegistration Metainformation about a fork and a method to generate it from parameters.
 */
public class ForkRegistration {

    private String name;
    private String description;
    private ForkConstructor forkConstructor;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ForkConstructor getForkConstructor() {
        return forkConstructor;
    }

    public void setForkConstructor(ForkConstructor forkConstructor) {
        this.forkConstructor = forkConstructor;
    }

}
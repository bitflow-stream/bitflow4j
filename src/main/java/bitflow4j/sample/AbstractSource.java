package bitflow4j.sample;

/**
 * Created by anton on 13.02.17.
 */
public abstract class AbstractSource implements Source {

    private Sink output;

    protected Sink output() {
        if (this.output == null) {
            throw new IllegalStateException("The output for this Source has not yet been initialized");
        }
        return output;
    }

    @Override
    public void setOutgoingSink(Sink sink) {
        if (this.output != null) {
            throw new IllegalStateException("This sink for this Source was already initialized");
        }
        this.output = sink;
    }

}

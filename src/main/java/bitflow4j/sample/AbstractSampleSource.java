package bitflow4j.sample;

/**
 * Created by anton on 13.02.17.
 */
public abstract class AbstractSampleSource implements SampleSource {

    private SampleSink output;

    protected SampleSink output() {
        if (this.output == null) {
            throw new IllegalStateException("The output for this SampleSource has not yet been initialized");
        }
        return output;
    }

    @Override
    public void setOutgoingSink(SampleSink sink) {
        if (this.output != null) {
            throw new IllegalStateException("This sink for this SampleSource was already initialized");
        }
        this.output = sink;
    }

}

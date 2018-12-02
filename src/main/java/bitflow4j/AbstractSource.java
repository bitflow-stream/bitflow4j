package bitflow4j;

/**
 * Created by anton on 13.02.17.
 */
public abstract class AbstractSource implements Source {

    protected PipelineStep output;

    protected PipelineStep output() {
        if (this.output == null) {
            throw new IllegalStateException("The output for this Source has not yet been initialized");
        }
        return output;
    }

    @Override
    public void setOutgoingSink(PipelineStep sink) {
        if (this.output != null) {
            throw new IllegalStateException(String.format("The sink for this Source was already initialized. Source: %s. Old sink: %s. New sink: %s",
                    this, this.output, sink));
        }
        this.output = sink;
    }

    @Override
    public void close() {
        output().close();
    }
}

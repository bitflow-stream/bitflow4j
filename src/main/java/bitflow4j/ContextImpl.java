package bitflow4j;

import java.io.IOException;

public class ContextImpl implements Context {

    private final SampleChannel channel;

    public ContextImpl(SampleChannel channel) {
        this.channel = channel;
    }

    public void outputSample(Sample sample) throws IOException {
        channel.writeSample(sample);
    }

}

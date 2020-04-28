package bitflow4j;

import java.io.IOException;

public interface Context {

    void outputSample(Sample sample) throws IOException;

}

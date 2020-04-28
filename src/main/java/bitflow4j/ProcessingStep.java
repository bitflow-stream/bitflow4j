package bitflow4j;

import java.io.IOException;

public interface ProcessingStep {

    String toString();

    void initialize(Context context) throws IOException;

    void handleSample(Sample sample) throws IOException;

    void cleanup() throws IOException;

}

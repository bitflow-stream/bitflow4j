package bitflow4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MockContext implements Context {

    public final List<Sample> samples = new ArrayList<>();

    @Override
    public void outputSample(Sample sample) throws IOException {
        samples.add(sample);
    }

}

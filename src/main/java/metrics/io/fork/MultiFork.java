package metrics.io.fork;

import metrics.Sample;
import metrics.io.MetricOutputStream;
import metrics.main.misc.ParameterHash;

import java.io.IOException;

/**
 * Forward every incoming sample to ALL sub-pipelines.
 *
 * Created by anton on 4/23/16.
 */
public class MultiFork extends AbstractFork<Integer> {

    private final int numOutputs;

    public MultiFork(int numOutputs, OutputStreamFactory<Integer> outputs) {
        super(outputs);
        this.numOutputs = numOutputs;
    }

    public MultiFork(int numOutputs) {
        super();
        this.numOutputs = numOutputs;
    }

    public MultiFork(MetricOutputStream ...outputs) {
        super();
        this.numOutputs = outputs.length;
        setOutputs(outputs);
    }

    public void setOutputs(MetricOutputStream ...outputs) {
        if (outputs.length != this.numOutputs) {
            throw new IllegalArgumentException("Expected number of outputs " + this.numOutputs + ", received " + outputs.length);
        }
        setOutputFactory((num) -> outputs[num]);
    }

    public void writeSample(Sample sample) throws IOException {
        for (int i = 0; i < numOutputs; i++) {
            getOutputStream(i).writeSample(sample);
        }
    }

    @Override
    public void hashParameters(ParameterHash hash) {
        super.hashParameters(hash);
        hash.writeInt(numOutputs);
    }
}

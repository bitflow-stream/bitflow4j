package metrics.algorithms;

import metrics.Sample;
import metrics.io.InputStreamClosedException;
import metrics.io.MetricInputStream;
import metrics.io.MetricOutputStream;

import java.io.IOException;

/**
 * This abstract algorithm reads all input samples without outputting any results
 * until the input stream is closed. Only after all input samples are read, the
 * results are written to the output stream.
 * This is not applicable for streaming inputs of indefinite length (like receiving over TCP),
 * and should only be used for finite inputs like CSV files.
 */
public abstract class PostAnalysisAlgorithm extends GenericAlgorithm {

    public PostAnalysisAlgorithm() {
        super();
    }

    public PostAnalysisAlgorithm(String name) {
        super(name);
    }

    protected void executeStep(MetricInputStream input, MetricOutputStream output) throws IOException {
        try {
            Sample sample = input.readSample();
            analyseSample(sample);
        } catch(InputStreamClosedException closedExc) {
            System.err.println("Starting analysis phase of " + getName());
            try {
                writeResults(output);
            } catch (Exception resultExc) {
                System.err.println("Error writing results of " + getName());
                resultExc.printStackTrace();
            }
            throw closedExc;
        }
    }

    protected abstract void analyseSample(Sample sample) throws IOException;

    protected abstract void writeResults(MetricOutputStream output) throws IOException;

}

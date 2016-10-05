package metrics.algorithms;

import metrics.algorithms.classification.Model;
import metrics.io.MetricInputStream;
import metrics.io.MetricOutputStream;
import metrics.io.MetricPipe;

import java.io.IOException;
import java.io.Serializable;

/**
 * This pseudo-algorithm waits until its input stream is closed, then takes the model from a given algorithm and puts
 * it into a given Model object. This Model object can be received by a AlgorithmModelReceiver.
 *
 * IMPORTANT: Only this wrapper algorithm should be part of the pipeline.
 *
 * Created by anton on 9/6/16.
 */
public class AlgorithmModelProvider<T extends Serializable> extends AbstractAlgorithm {

    private final AbstractAlgorithm wrapped;
    private final Model<T> model;
    private final MetricPipe pipe = new MetricPipe();

    public AlgorithmModelProvider(AbstractAlgorithm wrapped, Model<T> model) {
        this.wrapped = wrapped;
        this.model = model;
    }

    @Override
    public synchronized void start(MetricInputStream input, MetricOutputStream output) throws IOException {
        super.start(input, pipe);
        wrapped.start(pipe, output);
    }

    @Override
    protected void inputClosed(MetricOutputStream output) throws IOException {
        System.err.println("Model-Provider closing input for " + wrapped.toString());
        pipe.close();
        System.err.println("Model-Provider is waiting for " + wrapped.toString() + " to finish...");
        pipe.waitUntilClosed();
        System.err.println(toString() + " is finished, now delivering the model.");
        //TODO changed api and deadlock
        T object = (T) wrapped.getModel();
        model.setModel(object);
        super.inputClosed(output);
    }

    public String toString() {
        return "Model-Provider for " + wrapped.toString();
    }

}

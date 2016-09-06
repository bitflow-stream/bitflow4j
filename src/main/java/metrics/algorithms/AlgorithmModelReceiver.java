package metrics.algorithms;

import metrics.algorithms.classification.Model;
import metrics.io.MetricInputStream;
import metrics.io.MetricOutputStream;
import metrics.main.misc.ParameterHash;

import java.io.IOException;
import java.io.Serializable;

/**
 * This algorithm wraps another algorithm and only starts it after receiving from a given Model object.
 * The received object is then given to the wrapped algorithm as model.
 * This allows prolonging the start of the wrapped algorithm until a required model is available.
 *
 * IMPORTANT: Only this wrapper algorithm should be part of the pipeline.
 *
 * Created by anton on 9/6/16.
 */
public class AlgorithmModelReceiver<T extends Serializable> implements Algorithm {

    private final AbstractAlgorithm wrapped;
    private final Model<T> model;

    public AlgorithmModelReceiver(AbstractAlgorithm wrapped, Model<T> model) {
        this.wrapped = wrapped;
        this.model = model;
    }

    @Override
    public void start(MetricInputStream input, MetricOutputStream output) {
        new Thread() {
            @Override
            public void run() {
                try {
                    System.err.println(AlgorithmModelReceiver.this.toString() + " waiting for model...");
                    T object = model.getModel();
                    System.err.println(AlgorithmModelReceiver.this.toString() + " received model, now starting " + wrapped.toString());
                    wrapped.setModel(object);
                    wrapped.start(input, output);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void hashParameters(ParameterHash hash) {
        wrapped.hashParameters(hash);
    }

    public String toString() {
        return "Model-Receiver for " + wrapped.toString();
    }

}

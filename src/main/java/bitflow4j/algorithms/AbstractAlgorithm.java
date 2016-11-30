package bitflow4j.algorithms;

import bitflow4j.Sample;
import bitflow4j.main.ParameterHash;

import java.io.IOException;
import java.util.logging.Logger;


public abstract class AbstractAlgorithm implements Algorithm {
    private static final Logger logger = Logger.getLogger(AbstractAlgorithm.class.getName());
    protected FilterImpl worker = null;

    /**
     * This method must be called by the Filter instance that is associated with this algorithm.
     * @param worker the worker
     */
    @Override
    public void init(FilterImpl worker) {
        this.worker = worker;
    }

    public String toString() {
        return getClass().getSimpleName() + " (Instance " + (worker == null ? ("not initialized") : (worker.id)) + ")";
    }


    public Sample writeSample(Sample sample) throws IOException {
        return sample;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public AlgorithmModel<?> getModel() {
        throw new UnsupportedOperationException("Not implemented for this class");
    }

    @Override
    public void setModel(AlgorithmModel<?> model) {
        throw new UnsupportedOperationException("Not implemented for this class");
    }

    @Deprecated
    public int getId() {
        return this.worker.id;
    }

    @Override
    @Deprecated
    public boolean equals(Object o) {
        return o instanceof AbstractAlgorithm ? ((AbstractAlgorithm) o).getId() == this.getId() : o instanceof Integer ? this.getId() == ((Integer) o).intValue() : false;
    }

    @Override
    public void hashParameters(ParameterHash hash) {
        //nothing
    }
}

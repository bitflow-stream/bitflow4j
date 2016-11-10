package bitflow4j.algorithms;

/**
 * Created by anton on 10.11.16.
 */
public class BaseAlgorithmModel<T> implements AlgorithmModel<T> {

    private final T modelObject;

    public BaseAlgorithmModel(T obj) {
        this.modelObject = obj;
    }

    @Override
    public T getModel() {
        return modelObject;
    }

}

package bitflow4j.steps.fork.distribute;

import bitflow4j.steps.fork.Distributor;
import bitflow4j.sample.Sample;

/**
 * Created by anton on 13.02.17.
 */
public class MultiplexDistributor implements Distributor {

    private final Object[] keys;

    public MultiplexDistributor(int numSubPipelines) {
        keys = new Object[numSubPipelines];
        for (int i = 0; i < numSubPipelines; i++) {
            keys[i] = i;
        }
    }

    @Override
    public Object[] distribute(Sample sample) {
        return keys;
    }

}

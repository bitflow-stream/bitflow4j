package bitflow4j.algorithms.fork.distribute;

import bitflow4j.sample.Sample;
import bitflow4j.algorithms.fork.ForkDistributor;

/**
 * Created by anton on 13.02.17.
 */
public class MultiplexDistributor implements ForkDistributor {

    private final Object[] keys;

    public MultiplexDistributor(int numSubpipelines) {
        keys = new Object[numSubpipelines];
        for (int i = 0; i < numSubpipelines; i++) {
            keys[i] = i;
        }
    }

    @Override
    public Object[] distribute(Sample sample) {
        return keys;
    }

}

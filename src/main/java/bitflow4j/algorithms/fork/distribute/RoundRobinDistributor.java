package bitflow4j.algorithms.fork.distribute;

import bitflow4j.sample.Sample;
import bitflow4j.algorithms.fork.ForkDistributor;

/**
 * Created by anton on 13.02.17.
 */
public class RoundRobinDistributor implements ForkDistributor {

    private final int numSubpipelines;
    private int counter = 0;

    public RoundRobinDistributor(int numSubpipelines) {
        this.numSubpipelines = numSubpipelines;
    }

    @Override
    public Object[] distribute(Sample sample) {
        return new Object[]{counter++ % numSubpipelines};
    }

}

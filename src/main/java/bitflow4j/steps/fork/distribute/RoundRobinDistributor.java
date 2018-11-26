package bitflow4j.steps.fork.distribute;

import bitflow4j.Sample;
import bitflow4j.steps.fork.Distributor;

/**
 * Created by anton on 13.02.17.
 */
public class RoundRobinDistributor implements Distributor {

    private final int numSubPipelines;
    private int counter = 0;

    public RoundRobinDistributor(int numSubPipelines) {
        this.numSubPipelines = numSubPipelines;
    }

    @Override
    public Object[] distribute(Sample sample) {
        return new Object[]{counter++ % numSubPipelines};
    }

}

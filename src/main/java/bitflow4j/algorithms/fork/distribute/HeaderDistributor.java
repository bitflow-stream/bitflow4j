package bitflow4j.algorithms.fork.distribute;

import bitflow4j.sample.Sample;
import bitflow4j.steps.fork.Distributor;

/**
 *
 * @author fschmidt
 */
public class HeaderDistributor implements Distributor {
  
    @Override
    public Object[] distribute(Sample sample) {
        return new Object[]{hash(sample.getHeader().header)};
    }

    private long hash(String[] values) {
        long result = 17;
        for (String v : values) {
            result = 37 * result + v.hashCode();
        }
        return result;
    }
}

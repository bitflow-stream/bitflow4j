package bitflow4j.steps.fork.distribute;

import bitflow4j.Sample;
import bitflow4j.steps.fork.Distributor;

/**
 *
 * @author fschmidt
 */
public class HeaderHashDistributor implements Distributor {
  
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

package bitflow4j.algorithms.fork;

import bitflow4j.sample.Sample;

/**
 * Created by anton on 13.02.17.
 */
public interface ForkDistributor {

    Object[] distribute(Sample sample);

}

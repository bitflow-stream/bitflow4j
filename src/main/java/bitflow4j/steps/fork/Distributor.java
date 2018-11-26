package bitflow4j.steps.fork;

import bitflow4j.Sample;

/**
 * Created by anton on 13.02.17.
 */
public interface Distributor {

    Object[] distribute(Sample sample);

}

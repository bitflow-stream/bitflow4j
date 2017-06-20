package bitflow4j.steps.fork.distribute;

import bitflow4j.steps.fork.Distributor;
import bitflow4j.sample.Sample;

import java.util.Random;

/**
 * Created by anton on 13.02.17.
 */
public class PortionDistributor implements Distributor {

    public static final Object MAIN_KEY = 0;
    public static final Object SECONDARY_KEY = 1;

    private final float redirectedPortion;
    private final Random rnd = new Random();

    public PortionDistributor(float redirectedPortion) {
        if (redirectedPortion < 0 || redirectedPortion > 1) {
            throw new IllegalArgumentException("redirectedPortion must be in 0..1: " + redirectedPortion);
        }
        this.redirectedPortion = redirectedPortion;
    }

    @Override
    public Object[] distribute(Sample sample) {
        Object key = rnd.nextFloat() < redirectedPortion ? MAIN_KEY : SECONDARY_KEY;
        return new Object[]{key};
    }

}
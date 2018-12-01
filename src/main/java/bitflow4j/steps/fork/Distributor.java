package bitflow4j.steps.fork;

import bitflow4j.Pipeline;
import bitflow4j.Sample;
import bitflow4j.misc.Pair;

import java.util.Collection;
import java.util.logging.Logger;

/**
 * Created by anton on 13.02.17.
 */
public interface Distributor {

    Logger logger = Logger.getLogger(Distributor.class.getName());

    /**
     * @return Each sample can be forwarded to multiple sub-pipelines, each of which is identified by a key string.
     * Left value: The Pipeline Key. Right value: Pipeline to send the sample into
     */
    Collection<Pair<String, Pipeline>> distribute(Sample sample);

}

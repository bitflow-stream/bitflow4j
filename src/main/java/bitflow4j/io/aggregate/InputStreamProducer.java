package bitflow4j.io.aggregate;

import bitflow4j.main.ParameterHash;

/**
 * Created by anton on 4/6/16.
 */
public interface InputStreamProducer {

    /**
     * Start producing instances of MetricInputStream and add them to
     * aggregator using addInput().
     * Before producing, also call producerStarting(), when no more
     * inputs will be produced, call producerFinished().
     */
    void start(MetricInputAggregator aggregator);

    default void hashParameters(ParameterHash hash) {
        hash.writeClassName(this);
    }

}

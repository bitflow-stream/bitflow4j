package MetricIO;

/**
 * Created by anton on 4/6/16.
 */
public interface InputStreamProducer {

    /**
     * Start producing instances of MetricInputStream and add them to
     * aggregator using addInput().
     *
     * @param aggregator
     */
    void start(MetricInputAggregator aggregator);

}

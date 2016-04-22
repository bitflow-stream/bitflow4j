package metrics.algorithms.logback;

/**
 * Created by anton on 4/21/16.
 */
public class NoNanMetricLog extends MetricLog {

    public NoNanMetricLog(String name) {
        super(name);
    }

    @Override
    double defaultValue() {
        return 0.0;
    }

    @Override
    double fillValue() {
        return 0.0;
    }
}

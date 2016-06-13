package metrics.io.plot;

import java.io.IOException;
import java.util.Map;

/**
 * Created by anton on 4/18/16.
 */
public abstract class AbstractPlotter<T extends DataContainer> {

    @Override
    public abstract String toString();

    public abstract void plotResult(OutputMetricPlotter.PlotType outputType, Map<String, T> map, String filename) throws IOException;

    public abstract T createDataContainer(int numDimensions, String label);

}

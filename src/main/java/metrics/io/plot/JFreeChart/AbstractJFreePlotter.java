package metrics.io.plot.JFreeChart;

import metrics.io.plot.AbstractPlotter;
import metrics.io.plot.OutputMetricPlotter;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.IOException;
import java.util.Map;

/**
 * Created by mwall on 18.04.16.
 */
public class AbstractJFreePlotter extends AbstractPlotter<JFreeDataContainer> {

    @Override
    public String toString() {
        return "JFree Plot";
    }

    @Override
    public void plotResult(OutputMetricPlotter.PlotType outputType, Map<String, JFreeDataContainer> map, String filename) throws IOException {


    }

    @Override
    public JFreeDataContainer createDataContainer(int numDimensions) {
        return new JFreeDataContainer(new XYSeriesCollection());
    }
}

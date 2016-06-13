package metrics.io.plot.JFreeChart;

import metrics.io.plot.DataContainer;
import org.jfree.data.xy.XYSeries;

/**
 * Created by mwall on 18.04.16.
 */
public class JFreeDataContainer implements DataContainer {

    public final XYSeries dataset;


    public JFreeDataContainer(XYSeries dataset) {
        this.dataset = dataset;
    }

    public XYSeries getChart() {
        return dataset;
    }

    @Override
    public void add(double... data) {
        this.dataset.add(data[0], data[1]);
    }
}

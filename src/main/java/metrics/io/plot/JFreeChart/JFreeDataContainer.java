package metrics.io.plot.JFreeChart;

import metrics.io.plot.DataContainer;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

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
        this.dataset.add(data[0],data[1]);
    }
}

package metrics.io.plot.JFreeChart;

import metrics.io.plot.DataContainer;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Created by mwall on 18.04.16.
 */
public class JFreeDataContainer implements DataContainer {

    public final XYSeriesCollection dataset;



    public JFreeDataContainer(XYSeriesCollection dataset) {
        this.dataset = dataset;
    }

    public XYSeriesCollection getChart() {
        return dataset;
    }

    @Override
    public void add(double... data) {

          //  this.datas

    }
}

package metrics.io.plot.plotFX;

import javafx.scene.chart.XYChart;
import metrics.io.plot.DataContainer;

/**
 * Created by mwall on 18.04.16.
 */
public class FxDataContainer implements DataContainer {

    public final XYChart.Series series;

    public FxDataContainer(XYChart.Series series) {
        this.series = series;
    }

    public XYChart.Series getSeries() {
        return series;
    }

    @Override
    public void add(double... data) {

        series.getData().add(new XYChart.Data(data[0], data[1]));
    }
}

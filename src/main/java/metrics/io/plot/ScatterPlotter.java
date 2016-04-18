package metrics.io.plot;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Orientation;
import de.erichseifert.gral.plots.XYPlot;

import java.io.IOException;
import java.util.Map;

/**
 * Created by mwall on 14.04.16.
 */
public class ScatterPlotter extends AbstractPlotter implements Plotter {

    @Override
    public String toString() {
        return "scatter plot";
    }

    @Override
    public void plotResult(OutputMetricPlotter.PlotType outputType, Map<String, DataTable> map, String filename) throws IOException {
        XYPlot plot = new XYPlot();
        for (final String key : map.keySet()) {
            DataTable a = map.get(key);
            DataSeries ds = new DataSeries(key, a);

            plot.add(ds);
            plot.getPointRenderers(ds).get(0).setColor(this.getNextColor());
        }

        plot.getLegend().setOrientation(Orientation.VERTICAL);
        plot.setLegendVisible(true);
        plot.getLegend().setAlignmentY(1.0);
        plot.getLegend().setAlignmentX(1.0);

        //plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
        plot.getTitle().setText(toString());

        // Output plot (Swing or file)
        showPlot(plot, outputType, filename);
    }

}

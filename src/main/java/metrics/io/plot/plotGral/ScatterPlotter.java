package metrics.io.plot.plotGral;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Orientation;
import de.erichseifert.gral.plots.XYPlot;
import metrics.io.plot.ColorGenerator;
import metrics.io.plot.OutputMetricPlotter;

import java.io.IOException;
import java.util.Map;

/**
 * Created by mwall on 14.04.16.
 */
public class ScatterPlotter extends AbstractGralPlotter {

    @Override
    public String toString() {
        return "scatter plotGral";
    }

    @Override
    public void plotResult(OutputMetricPlotter.PlotType outputType, Map<String, GralDataContainer> map, String filename) throws IOException {
        XYPlot plot = new XYPlot();
        for (final String key : map.keySet()) {
            DataTable a = map.get(key).table;
            DataSeries ds = new DataSeries(key, a);

            plot.add(ds);
            ColorGenerator cg = new ColorGenerator();
            plot.getPointRenderers(ds).get(0).setColor(cg.getNextColor());
        }

        plot.getLegend().setOrientation(Orientation.VERTICAL);
        //plotGral.setLegendDistance(2);
        plot.setLegendVisible(true);
        plot.getLegend().setAlignmentY(1.0);
        plot.getLegend().setAlignmentX(1.0);

        //plotGral.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
        plot.getTitle().setText(toString());

        // Output plotGral (Swing or file)
        showPlot(plot, outputType, filename);
    }

}

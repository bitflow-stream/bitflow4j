package metrics.io.plot.plotGral;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.EnumeratedData;
import de.erichseifert.gral.data.statistics.Histogram1D;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.graphics.Orientation;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.MathUtils;
import metrics.io.plot.OutputMetricPlotter;

import java.io.IOException;
import java.util.Map;

/**
 * Created by mwall on 14.04.16.
 */
public class HistogramPlotter extends AbstractGralPlotter {

    @Override
    public String toString() {
        return "histogram";
    }

    @Override
    public void plotResult(OutputMetricPlotter.PlotType outputType, Map<String, GralDataContainer> map, String filename) throws IOException {

        // Create histogram from data
        //for (final String key : map.keySet()) {
        DataTable data = map.get("load").table;

        Histogram1D histogram = new Histogram1D(data, Orientation.VERTICAL, new Number[]{1000});
        // Create a second dimension (x axis) for plotting
        DataSource histogram2d = new EnumeratedData(histogram);

        // Create new bar plotGral
        BarPlot plot = new BarPlot(histogram2d);

        // Format plotGral
        //plotGral.setInsets(new Insets2D.Double(20.0, 65.0, 50.0, 40.0));
        plot.getTitle().setText(
                String.format("Distribution of %d random samples", data.getRowCount()));
        plot.setBarWidth(0.78);

        // Format x axis
        plot.getAxisRenderer(BarPlot.AXIS_X).setTickAlignment(1.0);
        plot.getAxisRenderer(BarPlot.AXIS_X).setTickSpacing(100000);
        plot.getAxisRenderer(BarPlot.AXIS_X).setMinorTicksVisible(true);
        // Format y axis
        plot.getAxis(BarPlot.AXIS_Y).setRange(0.0,
                MathUtils.ceil(histogram.getStatistics().get(Statistics.MAX) * 1.1, 25.0));
        plot.getAxisRenderer(BarPlot.AXIS_Y).setTickAlignment(0.0);
        plot.getAxisRenderer(BarPlot.AXIS_Y).setMinorTicksVisible(true);
        plot.getAxisRenderer(BarPlot.AXIS_Y).setIntersection(-4.4);

        // Format bars
        PointRenderer barRenderer = plot.getPointRenderers(histogram2d).get(0);
        barRenderer.setColor(GraphicsUtils.deriveWithAlpha(this.getNextColor(), 128));
        barRenderer.setValueVisible(true);

        showPlot(plot, outputType, filename);
    }

}

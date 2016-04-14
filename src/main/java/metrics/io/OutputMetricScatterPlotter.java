package metrics.io;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.ui.InteractivePanel;
import metrics.Sample;

import java.awt.*;
import java.io.IOException;

/**
 * Created by mwall on 13.04.16.
 */
public class OutputMetricScatterPlotter extends PlotPanel implements MetricOutputStream {

    private final int xColumn;
    private final int yColumn;
    private final DataTable data = new DataTable(Double.class, Double.class);

    public OutputMetricScatterPlotter(int xColumn, int yColumn) {
        this.xColumn = xColumn;
        this.yColumn = yColumn;
    }

    private void plotResult() {
        XYPlot plot = new XYPlot(data);
        // Format plot
        //plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
        plot.getTitle().setText(getDescription());

        // Format points
        plot.getPointRenderers(data).get(0).setColor(COLOR1);

        // Add plot to Swing component
        add(new InteractivePanel(plot), BorderLayout.CENTER);

        this.showInFrame();
    }

    @Override
    public String getTitle() {
        return "Test-Plot";
    }

    @Override
    public String getDescription() {
        return "description";
    }

    public void writeSample(Sample sample) throws IOException {
        double[] values = sample.getMetrics();
        data.add(getValue(values, xColumn), getValue(values, yColumn));
    }

    private double getValue(double[] values, int index) {
        if (index >= values.length) {
            return 0.0;
        }
        return values[index];
    }

    public void close() throws IOException {
        this.plotResult();
    }

}

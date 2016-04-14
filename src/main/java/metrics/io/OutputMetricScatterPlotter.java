package metrics.io;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.ui.InteractivePanel;
import metrics.Sample;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import de.erichseifert.gral.examples.ExamplePanel;


/**
 * Created by mwall on 13.04.16.
 */
public class OutputMetricScatterPlotter extends PlotPanel implements MetricOutputStream  {

    private int xColumn;
    private int yColumn;

    private List<Double> xList = null;
    private List<Double> yList = null;


    public OutputMetricScatterPlotter(int xColumn, int yColumn){
        this.xColumn = xColumn;
        this.yColumn = yColumn;
        xList = new ArrayList<Double>();
        yList = new ArrayList<Double>();
    }


    private void plotResult(){

        DataTable data = new DataTable(Double.class, Double.class);
        for (int i = 0; i < xList.size(); i++) {
            data.add(xList.get(i), yList.get(i));
        }
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
        this.xList.add(sample.getMetrics()[xColumn]);
        int yCol = yColumn >= sample.getMetrics().length ? sample.getMetrics().length - 1 : yColumn;
        this.yList.add(sample.getMetrics()[yCol]);
    }

    public void close() throws IOException {
        this.plotResult();
    }

}

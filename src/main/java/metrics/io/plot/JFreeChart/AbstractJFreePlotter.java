package metrics.io.plot.JFreeChart;

import metrics.io.plot.AbstractPlotter;
import metrics.io.plot.ColorGenerator;
import metrics.io.plot.OutputMetricPlotter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;
import java.io.IOException;
import java.util.Map;

/**
 * Created by mwall on 18.04.16.
 */
public class AbstractJFreePlotter extends AbstractPlotter<JFreeDataContainer> {

    XYSeriesCollection xyDataset;

    @Override
    public String toString() {
        return "JFree Plot";
    }

    @Override
    public void plotResult(OutputMetricPlotter.PlotType outputType, Map<String, JFreeDataContainer> map, String filename) throws IOException {


        final XYSeriesCollection collection = new XYSeriesCollection();

        for (Map.Entry<String, JFreeDataContainer> entry : map.entrySet()) {
            //entry.getValue().dataset.setDescription(entry.getKey());
            collection.addSeries(entry.getValue().dataset);
        }
        this.xyDataset = collection;
        XYChart chart = new XYChart("Data-analyse Chart Viewer", "Chart Title");
        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);
    }


    public class XYChart extends ApplicationFrame {
        public XYChart(String applicationTitle, String chartTitle) {
            super(applicationTitle);
            JFreeChart xylineChart = ChartFactory.createXYLineChart(
                    chartTitle,
                    "Category",
                    "Score",
                    xyDataset,
                    PlotOrientation.VERTICAL,
                    true, true, false);

            ChartPanel chartPanel = new ChartPanel(xylineChart);
            chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
            final XYPlot plot = xylineChart.getXYPlot();
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            ColorGenerator cg = new ColorGenerator();
            for (int i = 0; i < xyDataset.getSeries().size(); i++) {
                renderer.setSeriesPaint(i, cg.getNextColor());
                renderer.setSeriesStroke(0, new BasicStroke(i));
            }
            plot.setRenderer(renderer);
            setContentPane(chartPanel);
        }
    }

    @Override
    public JFreeDataContainer createDataContainer(int numDimensions, String label) {
        return new JFreeDataContainer(new XYSeries(label));
    }
}

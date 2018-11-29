package bitflow4j.steps.visualization;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fschmidt
 */
public class HistogramAlgorithm extends AbstractPipelineStep {

    private final Map<String, List<Double>> data;
    private final Map<String, Double> maxValue;
    private final Map<String, Double> minValue;
    private boolean firstSample;
    private String postfix = "histogram";
    private final String baseFilePath;

    public HistogramAlgorithm(String baseFilePath, String postfix) {
        data = new HashMap<>();
        this.baseFilePath = baseFilePath;
        maxValue = new HashMap<>();
        minValue = new HashMap<>();
        firstSample = true;
        this.postfix = postfix;
    }

    public HistogramAlgorithm(String baseFilePath) {
        data = new HashMap<>();
        this.baseFilePath = baseFilePath;
        maxValue = new HashMap<>();
        minValue = new HashMap<>();
        firstSample = true;
    }

    private void init(Sample sample) {
        for (String metric : sample.getHeader().header) {
            data.put(metric, new ArrayList<>());
            maxValue.put(metric, Double.MIN_VALUE);
            minValue.put(metric, Double.MAX_VALUE);
        }
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        if (firstSample) {
            init(sample);
            firstSample = false;
        }

        for (int i = 0; i < sample.getMetrics().length; i++) {
            String metricName = sample.getHeader().header[i];
            double metricValue = sample.getMetrics()[i];
            data.get(metricName).add(metricValue);
            if (maxValue.get(metricName) < metricValue) {
                maxValue.put(metricName, metricValue);
            }
            if (minValue.get(metricName) > metricValue) {
                maxValue.put(metricName, metricValue);
            }
        }
        super.writeSample(sample);
    }

    @Override
    protected void doClose() throws IOException {

        for (String metricName : data.keySet()) {
            double[] dataArray = null;
            if (data.containsKey(metricName)) {
                dataArray = data.get(metricName).stream().mapToDouble(d -> d).toArray();
            }

            HistogramDataset dataset = new HistogramDataset();
            dataset.setType(HistogramType.RELATIVE_FREQUENCY);
            if (dataArray != null && dataArray.length > 0) {
                dataset.addSeries("other", dataArray, 50);
            }
            String plotTitle = "Histogram";
            String yAxis = "count";
            PlotOrientation orientation = PlotOrientation.VERTICAL;
            JFreeChart chart = ChartFactory.createHistogram(plotTitle, metricName, yAxis,
                    dataset, orientation, true, false, false);

            XYPlot plot = (XYPlot) chart.getPlot();
            XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
            renderer.setBarPainter(new StandardXYBarPainter());
            // translucent red, green & blue
            Paint[] paintArray = {
                    Color.GREEN,
                    Color.RED,
                    Color.BLUE
            };
            plot.setDrawingSupplier(new DefaultDrawingSupplier(
                    paintArray,
                    DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
                    DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
                    DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
                    DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                    DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));

            try {
                int width = 600;
                int height = 600;
                String newMetricName = metricName;
                newMetricName = newMetricName.replaceAll("/", "-");
                ChartUtilities.saveChartAsPNG(new File(baseFilePath + "/" + newMetricName + "_" + postfix + ".png"), chart, width, height);
            } catch (IOException e) {
                System.out.println("FAIL save");
            }
        }

        super.doClose();
    }

}

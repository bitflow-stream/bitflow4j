package bitflow4j.steps.visualization;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;
import bitflow4j.window.MetricStatisticsWindow;
import bitflow4j.window.MultiHeaderWindow;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author fschmidt
 */
public class TimeSeriesPlotter extends AbstractPipelineStep {

    private final MultiHeaderWindow<MetricStatisticsWindow> window = new MultiHeaderWindow<>(MetricStatisticsWindow.FACTORY);
    private final List<Date> timestamps = new ArrayList<>();
    private final String baseFilePath;
    private final int numSamplesToSkip;
    private int skipCounter = 0;

    public TimeSeriesPlotter(String baseFilePath) {
        this.baseFilePath = baseFilePath;
        numSamplesToSkip = 0;
    }

    public TimeSeriesPlotter(String baseFilePath, int numSamplesToSkip) {
        this.baseFilePath = baseFilePath;
        this.numSamplesToSkip = numSamplesToSkip;
        skipCounter = numSamplesToSkip;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        if (skipCounter == numSamplesToSkip) {
            window.add(sample);
            timestamps.add(sample.getTimestamp());
            skipCounter = 0;
        }else{
            skipCounter++;
        }
        super.writeSample(sample);
    }

    @Override
    protected void doClose() throws IOException {
        for (MetricStatisticsWindow metricWindow : window.allMetricWindows()) {
            final TimeSeries series = new TimeSeries(metricWindow.name);
            for (int i = 0; i < timestamps.size(); i++) {
                series.addOrUpdate(new FixedMillisecond(timestamps.get(i)), metricWindow.getVector()[i]);
            }
            XYDataset dataset = new TimeSeriesCollection(series);
            JFreeChart chart = ChartFactory.createTimeSeriesChart("Timeseries", "time", metricWindow.name, dataset, false, false, false);
            try {
                int width = 1600;
                int height = 400;
                String newMetricName = metricWindow.name;
                newMetricName = newMetricName.replaceAll("/", "-");
                ChartUtilities.saveChartAsPNG(new File(baseFilePath + "/" + newMetricName + "_timeseries.png"), chart, width, height);
            } catch (IOException e) {
                System.out.println("FAIL save");
            }
        }

        super.doClose();
    }

}

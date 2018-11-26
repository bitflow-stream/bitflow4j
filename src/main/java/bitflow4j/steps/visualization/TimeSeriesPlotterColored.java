package bitflow4j.steps.visualization;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;
import bitflow4j.window.MetricStatisticsWindow;
import bitflow4j.window.MultiHeaderWindow;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author fschmidt
 */
public class TimeSeriesPlotterColored extends AbstractPipelineStep {

    private final MultiHeaderWindow<MetricStatisticsWindow> window = new MultiHeaderWindow<>(MetricStatisticsWindow.FACTORY);
    private final List<Date> timestamps = new ArrayList<>();
    private final String baseFilePath;
    private final String baseFilePrefix;
    private final int numSamplesToSkip;
    private int skipCounter = 0;

    public TimeSeriesPlotterColored(String baseFilePath) {
        this.baseFilePath = baseFilePath;
        numSamplesToSkip = 0;
        this.baseFilePrefix="";
    }

    public TimeSeriesPlotterColored(String baseFilePath, int numSamplesToSkip,String baseFilePrefix) {
        this.baseFilePath = baseFilePath;
        this.numSamplesToSkip = numSamplesToSkip;
        skipCounter = numSamplesToSkip;
        this.baseFilePrefix=baseFilePrefix;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        if (skipCounter == numSamplesToSkip) {
            //System.out.println(sample);
            window.add(sample);
            timestamps.add(sample.getTimestamp());
            skipCounter = 0;
        } else {
            skipCounter++;
        }
        super.writeSample(sample);
    }

    @Override
    protected void doClose() throws IOException {
        for (MetricStatisticsWindow metricWindow : window.allMetricWindows()) {
            List<IntervalMarker> seriesMarker = new ArrayList<>();
            IntervalMarker currentMarker = null;
            int currentMode = -1; //0:n-n, 1:a-a, 2:n-a, 3:a-n
            for (int i = 0; i < metricWindow.values.size(); i++) {
                Sample s = window.getSample(i);
                if (s.hasTag("actual") && s.hasTag("prediction")) {
                    if (s.getTag("actual").equals("normal") && s.getTag("prediction").equals("normal")) {
                        if (currentMode == 0) {
                            currentMarker.setEndValue(s.getTimestamp().getTime());
                        } else {
                            currentMode = 0;
                            currentMarker = new IntervalMarker(s.getTimestamp().getTime(), s.getTimestamp().getTime());
                            currentMarker.setPaint(Color.GREEN);
                            currentMarker.setAlpha(0.45f);
                            seriesMarker.add(currentMarker);
                        }
                    } else if (s.getTag("actual").equals("anomaly") && s.getTag("prediction").equals("anomaly")) {
                        if (currentMode == 1) {
                            currentMarker.setEndValue(s.getTimestamp().getTime());
                        } else {
                            currentMode = 1;
                            currentMarker = new IntervalMarker(s.getTimestamp().getTime(), s.getTimestamp().getTime());
                            currentMarker.setPaint(Color.YELLOW);
                            currentMarker.setAlpha(0.45f);
                            seriesMarker.add(currentMarker);
                        }
                    } else if (s.getTag("actual").equals("normal") && s.getTag("prediction").equals("anomaly")) {
                        if (currentMode == 2) {
                            currentMarker.setEndValue(s.getTimestamp().getTime());
                        } else {
                            currentMode = 2;
                            currentMarker = new IntervalMarker(s.getTimestamp().getTime(), s.getTimestamp().getTime());
                            currentMarker.setPaint(Color.RED);
                            currentMarker.setAlpha(0.45f);
                            seriesMarker.add(currentMarker);
                        }
                    } else if (s.getTag("actual").equals("anomaly") && s.getTag("prediction").equals("normal")) {
                        if (currentMode == 3) {
                            currentMarker.setEndValue(s.getTimestamp().getTime());
                        } else {
                            currentMode = 3;
                            currentMarker = new IntervalMarker(s.getTimestamp().getTime(), s.getTimestamp().getTime());
                            currentMarker.setPaint(Color.BLUE);
                            currentMarker.setAlpha(0.45f);
                            seriesMarker.add(currentMarker);
                        }
                    }
                } else if (s.hasTag("prediction") && !s.hasTag("actual")) {
                    if (s.getTag("prediction").equals("normal")) {
                        if (currentMode == 0) {
                            currentMarker.setEndValue(s.getTimestamp().getTime());
                        } else {
                            currentMode = 0;
                            currentMarker = new IntervalMarker(s.getTimestamp().getTime(), s.getTimestamp().getTime());
                            currentMarker.setPaint(Color.GREEN);
                            currentMarker.setAlpha(0.15f);
                            seriesMarker.add(currentMarker);
                        }
                    } else if (s.getTag("prediction").equals("anomaly")) {
                        if (currentMode == 1) {
                            currentMarker.setEndValue(s.getTimestamp().getTime());
                        } else {
                            currentMode = 1;
                            currentMarker = new IntervalMarker(s.getTimestamp().getTime(), s.getTimestamp().getTime());
                            currentMarker.setPaint(Color.RED);
                            currentMarker.setAlpha(0.15f);
                            seriesMarker.add(currentMarker);
                        }
                    }
                }
            }

            final TimeSeries series = new TimeSeries(metricWindow.name);
            for (int i = 0; i < timestamps.size(); i++) {
//                if (series.getDataItem(new FixedMillisecond(timestamps.get(i))) != null) {
//                    System.out.println("Same time period " + timestamps.get(i));
//                }
                series.addOrUpdate(new FixedMillisecond(timestamps.get(i)), metricWindow.getVector()[i]);
            }
            XYDataset dataset = new TimeSeriesCollection(series);

            JFreeChart chart = ChartFactory.createTimeSeriesChart("Timeseries", "time", metricWindow.name, dataset, false, false, false);
            for (IntervalMarker marker : seriesMarker) {
                chart.getXYPlot().addDomainMarker(marker);
            }
            try {
                int width = 8000;
                int height = 400;
                String newMetricName = metricWindow.name;
                newMetricName = newMetricName.replaceAll("/", "-");
                ChartUtilities
                        .saveChartAsPNG(new File(baseFilePath +"/"+ baseFilePrefix + newMetricName + "_colored_timeseries.png"), chart, width, height);
            } catch (IOException e) {
                System.out.println("FAIL save: " + e.toString());
            }
        }

        super.doClose();
    }

}

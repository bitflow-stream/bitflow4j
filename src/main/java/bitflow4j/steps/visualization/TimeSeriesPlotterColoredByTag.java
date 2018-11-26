package bitflow4j.steps.visualization;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;
import bitflow4j.window.MetricStatisticsWindow;
import bitflow4j.window.MultiHeaderWindow;
import bitflow4j.window.SampleWindow;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Vincent Hennig on 03.11.17. This Class will create one colored timeseries plot for each metric. Samples will be colored according
 * to the value of a given tag choosing a different color for each different value up to 20 different colors
 */
public class TimeSeriesPlotterColoredByTag extends AbstractPipelineStep {

    private static final Logger logger = Logger.getLogger(TimeSeriesPlotterColoredByTag.class.getName());
    private final SampleWindow sampleWindow = new SampleWindow();
    private final MultiHeaderWindow<MetricStatisticsWindow> metricsWindow = new MultiHeaderWindow<>(MetricStatisticsWindow.FACTORY);
    private final List<Date> timestamps = new ArrayList<>();
    private final String baseFilePath, colorTag;
    private final int numSamplesToSkip, numSamplesToSkipAtStart;
    private final int imageWidth, imageHeight;
    private int skipCounter, beginningSkipCounter = 0;
    private Map<String, Color> knownTagValues;
    public static String[] colors = new String[]{"#0000ff", "#00ff00", "#ff0000", "#c5b0d5", "#ff7f0e", "#ffbb78",
        "#2ca02c", "#98df8a", "#d62728", "#ff9896", "#9467bd", "#aec7e8", "#8c564b", "#c49c94",
        "#e377c2", "#f7b6d2", "#7f7f7f", "#c7c7c7", "#bcbd22", "#dbdb8d", "#17becf", "#9edae5"};
    private int currentColor = 0;

    public TimeSeriesPlotterColoredByTag(String baseFilePath, String colorTag) {
        this(baseFilePath, 0, 0, colorTag, 1600, 400);
    }

    public TimeSeriesPlotterColoredByTag(String baseFilePath, int numSamplesToSkip, String colorTag) {
        this(baseFilePath, numSamplesToSkip, 0, colorTag, 1600, 400);
    }

    public TimeSeriesPlotterColoredByTag(String baseFilePath, int numSamplesToSkip, int numSamplesToSkipAtStart, String colorTag) {
        this(baseFilePath, numSamplesToSkip, numSamplesToSkipAtStart, colorTag, 1600, 400);
    }

    public TimeSeriesPlotterColoredByTag(String baseFilePath, int numSamplesToSkip, int numSamplesToSkipAtStart, String colorTag,
            int imageWidth, int imageHeight) {
        if (!baseFilePath.endsWith(File.separator)) {
            this.baseFilePath = baseFilePath + File.separator;
        } else {
            this.baseFilePath = baseFilePath;
        }
        this.numSamplesToSkip = numSamplesToSkip;
        this.numSamplesToSkipAtStart = numSamplesToSkipAtStart;
        skipCounter = numSamplesToSkip;
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
        this.colorTag = colorTag;
        knownTagValues = new HashMap<>();
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        if (beginningSkipCounter == numSamplesToSkipAtStart) {
            if (skipCounter == numSamplesToSkip) {
                sampleWindow.add(sample);
                metricsWindow.add(sample);
                timestamps.add(sample.getTimestamp());
                skipCounter = 0;
                String tagValue = sample.getTag(colorTag);
                if (tagValue != null && !knownTagValues.containsKey(tagValue)) {
                    knownTagValues.put(tagValue, getUniqueColor());
                }
            } else {
                skipCounter++;
            }
        } else {
            beginningSkipCounter++;
        }
        super.writeSample(sample);
    }

    protected Color getUniqueColor() {
        if (currentColor == colors.length) {
            currentColor = 0;
        }
        return Color.decode(colors[currentColor++]);
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected void doClose() throws IOException {
        String currentTagValue = null;
        IntervalMarker currentMarker = null;
        List<IntervalMarker> markers = new ArrayList<>();
        long lastTimeStamp = 0;long totallastTimeStamp = 0;
        for (Sample s : sampleWindow.samples) {
            String tagValue = s.getTag(colorTag);
            if (tagValue == null) {
                continue;
            }
            
            if (currentMarker == null) {
                lastTimeStamp = s.getTimestamp().getTime();
                currentMarker = new IntervalMarker(lastTimeStamp, lastTimeStamp);
                currentMarker.setAlpha(0.2f);
                currentMarker.setPaint(knownTagValues.get(tagValue));
                markers.add(currentMarker);
                currentTagValue = tagValue;
            } else {
                totallastTimeStamp=s.getTimestamp().getTime();
                //Check for change
                if (!tagValue.equals(currentTagValue)) {
                    currentMarker.setEndValue(s.getTimestamp().getTime()-1l);
                    lastTimeStamp = s.getTimestamp().getTime();
                    currentMarker = new IntervalMarker(lastTimeStamp, lastTimeStamp);
                    currentMarker.setAlpha(0.2f);
                    currentMarker.setPaint(knownTagValues.get(tagValue));
                    markers.add(currentMarker);
                }
            }
        }
        if (currentMarker != null) {
            currentMarker.setEndValue(totallastTimeStamp-1l);
        }

        for (MetricStatisticsWindow metricWindow : metricsWindow.allMetricWindows()) {
            final TimeSeries series = new TimeSeries(metricWindow.name);
            for (int i = 0; i < timestamps.size(); i++) {
                series.addOrUpdate(new FixedMillisecond(timestamps.get(i)), metricWindow.getVector()[i]);
            }
            XYDataset dataset = new TimeSeriesCollection(series);

            JFreeChart chart = ChartFactory.createTimeSeriesChart("Timeseries", "time", metricWindow.name, dataset, true, false, false);
            XYPlot plot = chart.getXYPlot();
            for (IntervalMarker marker : markers) {
                plot.addDomainMarker(marker);
            }

            try {
                String newMetricName = metricWindow.name;
                newMetricName = newMetricName.replaceAll("/", "-");
                ChartUtilities
                        .saveChartAsPNG(new File(baseFilePath + newMetricName + "_colored_timeseries.png"), chart, imageWidth, imageHeight);
            } catch (IOException e) {
                System.out.println("FAIL save: " + e.toString());
            }
        }

        super.doClose();
    }
}

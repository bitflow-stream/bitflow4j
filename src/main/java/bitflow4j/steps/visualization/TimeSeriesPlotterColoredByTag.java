package bitflow4j.steps.visualization;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Header;
import bitflow4j.Sample;
import bitflow4j.script.registry.Description;
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
@Description("This Processing Step will create one colored timeseries plot for each metric.")
public class TimeSeriesPlotterColoredByTag extends AbstractPipelineStep {

    private static final Logger logger = Logger.getLogger(TimeSeriesPlotterColoredByTag.class.getName());
    private final List<Sample> sampleWindow = new ArrayList<>();
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

    @Override
    protected void doClose() throws IOException {
        String currentTagValue = null;
        IntervalMarker currentMarker = null;
        List<IntervalMarker> markers = new ArrayList<>();
        long lastTimeStamp;
        long totalLastTimeStamp = 0;
        for (Sample s : sampleWindow) {
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
                totalLastTimeStamp = s.getTimestamp().getTime();
                //Check for change
                if (!tagValue.equals(currentTagValue)) {
                    currentMarker.setEndValue(s.getTimestamp().getTime() - 1L);
                    lastTimeStamp = s.getTimestamp().getTime();
                    currentMarker = new IntervalMarker(lastTimeStamp, lastTimeStamp);
                    currentMarker.setAlpha(0.2f);
                    currentMarker.setPaint(knownTagValues.get(tagValue));
                    markers.add(currentMarker);
                }
            }
        }
        if (currentMarker != null) {
            currentMarker.setEndValue(totalLastTimeStamp - 1L);
        }


        Header header = sampleWindow.get(0).getHeader();
        for (int i = 0; i < header.numFields(); i++) {
            String fieldName = header.header[i];

            final TimeSeries series = new TimeSeries(fieldName);
            for (int j = 0; j < timestamps.size(); j++) {
                series.addOrUpdate(new FixedMillisecond(timestamps.get(j)), sampleWindow.get(j).getMetrics()[i]);
            }
            XYDataset dataset = new TimeSeriesCollection(series);

            JFreeChart chart = ChartFactory.createTimeSeriesChart("Timeseries", "time", fieldName, dataset, true, false, false);
            XYPlot plot = chart.getXYPlot();
            for (IntervalMarker marker : markers) {
                plot.addDomainMarker(marker);
            }

            try {
                String newMetricName = fieldName;
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

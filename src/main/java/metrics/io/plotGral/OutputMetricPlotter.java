package metrics.io.plotGral;

import de.erichseifert.gral.data.DataTable;
import metrics.Sample;
import metrics.io.AbstractOutputStream;
import metrics.io.MetricOutputStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mwall on 14.04.16.
 */
public class OutputMetricPlotter extends AbstractOutputStream implements MetricOutputStream {

    public enum PlotType {
        IN_FRAME,
        AS_FILE,
        AS_FILE_AND_IN_FRAME
    }

    private final int[] columns;
    private final Map<String, DataTable> colorMap;
    private final PlotType outputType;
    private final Plotter plotter;
    private final String filename;

    public OutputMetricPlotter(Plotter plotter, String filename, int ...columns) {
        this(plotter, PlotType.AS_FILE, filename, columns);
    }

    public OutputMetricPlotter(Plotter plotter, int ...columns) {
        this(plotter, PlotType.IN_FRAME, columns);
    }

    public OutputMetricPlotter(Plotter plotter, PlotType outputType, int ...columns) {
        this(plotter, outputType, null, columns);
    }

    /**
     * @param columns is an array of colums used from data sheet, you also define dimensions with this variable
     */
    public OutputMetricPlotter(Plotter plotter, PlotType outputType, String filename, int ...columns) {
        if (columns.length < 1 || columns.length > 2) {
            throw new IllegalArgumentException("Only 1D and 2D plots are supported.");
        }
        this.plotter = plotter;
        this.columns = columns;
        this.filename = filename;
        this.outputType = outputType;
        this.colorMap = new HashMap<>();
    }

    public void writeSample(Sample sample) throws IOException {
        String label = sample.getSource();
        double[] values = sample.getMetrics();
        switch (columns.length) {

            // 1D Plots
            case 1:
                if (!this.colorMap.containsKey(label)) {
                    DataTable data1d = new DataTable(Double.class);
                    this.colorMap.put(label, data1d);
                }
                DataTable data1d = this.colorMap.get(label);
                double xVal1d = getValue(values, columns[0]);
                data1d.add(xVal1d);
                break;

            // 2d Plots
            case 2:
                if (!this.colorMap.containsKey(label)) {
                    DataTable data2d = new DataTable(Double.class, Double.class);
                    this.colorMap.put(label, data2d);
                }
                DataTable data2d = this.colorMap.get(label);
                double xVal2d = getValue(values, columns[0]);

                if (values.length == 1 || columns[1] < 0) {
                    data2d.add(xVal2d, xVal2d);
                } else {
                    data2d.add(xVal2d, getValue(values, columns[1]));
                }
                break;
        }
    }

    private double getValue(double[] values, int index) throws IOException{
        if (index >= values.length) {
            throw new IOException("Not enough metrics in sample (need index " + index + ", have " + values.length + ")");
        }
        return values[index];
    }

    public void close() throws IOException {
        System.err.println("Plotting data with " + this.plotter.toString());
        this.plotter.plotResult(this.outputType, this.colorMap, this.filename);
        super.close();
    }

}

package metrics.io.plot;

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
    private int[] columns;

    private Map<String, DataTable> colorMap;

    private int outputType = 0;

    public static final int IN_FRAME = 0;
    public static final int AS_FILE = 1;
    public static final int AS_FILE_AND_IN_FRAME = 2;

    private int[] color = {0, 0, 0};

    private Plotter plotter;
    private String filename = null;

    public OutputMetricPlotter(int[] columns, Plotter plotter, String filename) {
        this(columns, plotter, OutputMetricPlotter.AS_FILE, filename);
    }


    public OutputMetricPlotter(int[] columns, Plotter plotter) {
        this(columns, plotter, OutputMetricPlotter.IN_FRAME);
    }

    public OutputMetricPlotter(int[] columns, Plotter plotter, int outputType) {
        this(columns, plotter, outputType, null);
    }

    /**
     * @param columns    is an array of colums used from data sheet, you also define dimensions with this variable
     * @param plotter
     * @param outputType
     * @param filename
     */

    private OutputMetricPlotter(int[] columns, Plotter plotter, int outputType, String filename) {
        System.err.println("Starting plot Results");
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
                    System.err.println("write " + label + " to Map");
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
                    System.err.println("write " + label + " to Map");
                    DataTable data2d = new DataTable(Double.class, Double.class);
                    this.colorMap.put(label, data2d);
                }
                DataTable data2d = this.colorMap.get(label);
                double xVal2d = getValue(values, columns[0]);

                if (columns[1] == -1) {
                    data2d.add(xVal2d, xVal2d);
                } else {
                    data2d.add(xVal2d, getValue(values, columns[1]));
                }
                break;
        }


    }

    private double getValue(double[] values, int index) {
        if (index >= values.length) {
            return 0.0;
        }
        return values[index];
    }

    public void close() throws IOException {
        this.plotter.plotResult(this.outputType, this.colorMap, this.filename);
        super.close();
    }

}

package metrics.io;

import de.erichseifert.gral.data.DataTable;
import metrics.Sample;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mwall on 14.04.16.
 */
public class OutputMetricPlotter extends AbstractOutputStream implements MetricOutputStream {
    private int xColumn;
    private int yColumn;

    private Map<String, DataTable> colorMap;

    private int outputType = 0;

    public static final int IN_FRAME = 0;
    public static final int AS_FILE = 1;
    public static final int AS_FILE_AND_IN_FRAME = 2;

    private int[] color = {0, 0, 0};

    private Plotter plotter;
    private String filename = null;

    public OutputMetricPlotter(int xColumn, int yColumn, Plotter plotter, String filename) {
        this(xColumn, yColumn, plotter, OutputMetricPlotter.AS_FILE, filename);
    }


    public OutputMetricPlotter(int xColumn, int yColumn, Plotter plotter) {
        this(xColumn, yColumn, plotter, OutputMetricPlotter.IN_FRAME);
    }

    public OutputMetricPlotter(int xColumn, int yColumn, Plotter plotter, int outputType) {
        this(xColumn, yColumn, plotter, OutputMetricPlotter.AS_FILE, null);
    }

    public OutputMetricPlotter(int xColumn, int yColumn, Plotter plotter, int outputType, String filename) {
        System.err.println("Starting plot Results");
        this.plotter = plotter;
        this.xColumn = xColumn;
        this.yColumn = yColumn;

        this.outputType = outputType;

        this.colorMap = new HashMap<>();

    }

    public void writeSample(Sample sample) throws IOException {
        String label = sample.getSource();

        if (!this.colorMap.containsKey(label)) {
            System.err.println("write " + label + " to Map");
            DataTable data = new DataTable(Double.class, Double.class);
            this.colorMap.put(label, data);
        }
        DataTable data = this.colorMap.get(label);
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
        this.plotter.plotResult(this.outputType, this.colorMap, this.filename);
        super.close();
    }

}

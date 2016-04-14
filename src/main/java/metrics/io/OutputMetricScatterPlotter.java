package metrics.io;

import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.graphics.Orientation;
import de.erichseifert.gral.io.plots.DrawableWriter;
import de.erichseifert.gral.io.plots.DrawableWriterFactory;
import metrics.Sample;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.*;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.ui.InteractivePanel;

import javax.swing.*;


/**
 * Created by mwall on 13.04.16.
 */
public class OutputMetricScatterPlotter extends PlotPanel implements MetricOutputStream {

    private int xColumn;
    private int yColumn;

    private Map<String,DataTable> colorMap;

    private int outputType = 0;

    public static final int IN_FRAME = 0;
    public static final int AS_FILE = 1;
    public static final int AS_FILE_AND_IN_FRAME = 2;

    private int[] color = {0,0,0};

    public OutputMetricScatterPlotter(int xColumn, int yColumn){
        this(xColumn,yColumn,OutputMetricScatterPlotter.IN_FRAME);
    }

    public OutputMetricScatterPlotter(int xColumn, int yColumn, int outputType) {
        System.err.println("Starting plot Results");
        this.xColumn = xColumn;
        this.yColumn = yColumn;

        this.outputType = outputType;

        this.colorMap = new HashMap<String,DataTable>();
    }
    private void plotResult(){
        //XYPlot plot = new XYPlot(colorMap.get("load"));
        XYPlot plot = new XYPlot();

        System.err.println("Adding DataTables to Plot");
        for (DataTable a : this.colorMap.values()) {
            plot.add(a);
            a.setName("testname");
            plot.getPointRenderers(a).get(0).setColor(this.getNextColor());
        }

        // Format legend
        plot.getLegend().setOrientation(Orientation.HORIZONTAL);
        plot.setLegendVisible(true);
        plot.getLegend().setAlignmentY(1.0);


        plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
        plot.getTitle().setText(getDescription());

        // Add plot to Swing component
        add(new InteractivePanel(plot), BorderLayout.CENTER);
        switch(outputType){
            case IN_FRAME:
                this.showInFrame();
                break;
            case AS_FILE:
                System.err.println("Save plot to file");
                this.save(plot);
                break;
            case AS_FILE_AND_IN_FRAME:
                this.save(plot);
                this.showInFrame();
                break;
        }
    }

    void save(XYPlot plot) {
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                DrawableWriter writer = DrawableWriterFactory.getInstance().get("application/postscript");
                writer.write(plot, new FileOutputStream(file), 800, 600);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private Color getNextColor(){
        this.color[0] = (color[0] + 32) % 256;
        this.color[1] = (color[1] + 128) % 256;
        this.color[2] = (color[2] + 64) % 256;
        Color rc = new Color(color[0],color[1],color[2]);

        return rc;
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
        String label = sample.getSource();

        if (!this.colorMap.containsKey(label)) {
            System.err.println("write " + label + " to Map");
            DataTable data = new DataTable(Double.class, Double.class);
            this.colorMap.put(label, data);
        }
        DataTable data = this.colorMap.get(label);
        double[] values = sample.getMetrics();
        data.add(getValue(values,xColumn),getValue(values,yColumn));
    }

    private double getValue(double[] values, int index) {
        if (index >= values.length) {
            return 0.0;
        }
        return values[index];
    }

    public void close() throws IOException {
        this.plotResult();
    }

}

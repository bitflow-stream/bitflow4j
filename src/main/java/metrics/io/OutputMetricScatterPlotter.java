package metrics.io;

import metrics.Sample;

import java.awt.*;
import java.io.IOException;

import java.util.*;

import de.erichseifert.gral.data.DataTable;
//import de.erichseifert.gral.examples.ExamplePanel;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.graphics.Insets2D;


/**
 * Created by mwall on 13.04.16.
 */
public class OutputMetricScatterPlotter extends PlotPanel implements MetricOutputStream  {

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
        XYPlot plot = new XYPlot();
        System.err.println("Adding DataTables to Plot");
        for (DataTable a : this.colorMap.values()) {
            plot.add(a);

            // Format points
            plot.getPointRenderers(a).get(0).setColor(this.getNextColor());
        }
        // Format plot
        //plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
        plot.getTitle().setText(getDescription());


        // Add plot to Swing component
        add(new InteractivePanel(plot), BorderLayout.CENTER);

        switch(outputType){
            case IN_FRAME:
                this.showInFrame();
                break;
            case AS_FILE:
                break;
            case AS_FILE_AND_IN_FRAME:
                break;
        }
    }

    private Color getNextColor(){
        this.color[0] = color[0] + 16 % 255;
        this.color[1] = color[1] + 32 % 255;
        this.color[2] = color[2] + 64 % 255;
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

    @Override
    public void writeSample(Sample sample) throws IOException {

        String label = sample.getLabel();

        if (this.colorMap.containsKey(label)){
            DataTable data = this.colorMap.get(label);
            data.add(sample.getMetrics()[xColumn],sample.getMetrics()[yColumn]);
        }else{
            DataTable data = new DataTable(Double.class, Double.class);
            this.colorMap.put(label,data);
        }
    }

    @Override
    public void close() throws IOException {
        this.plotResult();
    }

}

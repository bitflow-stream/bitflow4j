package metrics.io;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.graphics.Orientation;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.ui.InteractivePanel;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mwall on 14.04.16.
 */
public class ScatterPlotter extends PlotPanel implements Plotter{
    @Override
    public String getTitle() {
        return "Titel";
    }

    @Override
    public String getDescription() {
        return "Description";
    }

    @Override
    public void plotResult(int outputType, Map<String, DataTable> map, String filename) {
        //XYPlot plot = new XYPlot(colorMap.get("load"));
        XYPlot plot = new XYPlot();
        System.err.println("Adding DataTables to Plot");
        for (final String key : map.keySet()){
            DataTable a = map.get(key);
            DataSeries ds = new DataSeries(key,a);

            plot.add(ds);
            plot.getPointRenderers(ds).get(0).setColor(this.getNextColor());
        }

        plot.getLegend().setOrientation(Orientation.VERTICAL);
        plot.setLegendVisible(true);
        //plot.getLegend().se
        plot.getLegend().setAlignmentY(1.0);
        plot.getLegend().setAlignmentX(1.0);


        plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
        plot.getTitle().setText(getDescription());

        System.err.println("Prepare output");
        // Add plot to Swing component
        add(new InteractivePanel(plot), BorderLayout.CENTER);
        switch (outputType) {
            case OutputMetricPlotter.IN_FRAME:
                this.showInFrame();
                break;
            case OutputMetricPlotter.AS_FILE:
                System.err.println("Save plot to file");
                this.save(plot,filename);
                break;
            case OutputMetricPlotter.AS_FILE_AND_IN_FRAME:
                this.save(plot,filename);
                this.showInFrame();
                break;
        }
    }
}

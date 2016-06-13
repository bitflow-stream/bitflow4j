package metrics.io.plot.plotGral;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.io.plots.DrawableWriter;
import de.erichseifert.gral.io.plots.DrawableWriterFactory;
import de.erichseifert.gral.plots.XYPlot;
import metrics.io.plot.AbstractPlotter;
import metrics.io.plot.OutputMetricPlotter;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by mwall on 18.04.16.
 */
public abstract class AbstractGralPlotter extends AbstractPlotter<GralDataContainer> {

    protected void save(XYPlot plot, String filename) throws IOException {

        File file;
        if (filename == null) {
            JFileChooser chooser = new JFileChooser();
            int option = chooser.showSaveDialog(null);
            if (option == JFileChooser.APPROVE_OPTION) {
                file = chooser.getSelectedFile();
            } else {
                throw new IOException("File save dialog cancelled");
            }
        } else {
            file = new File(filename);
        }
        DrawableWriter writer = DrawableWriterFactory.getInstance().get("image/png");
        writer.write(plot, new FileOutputStream(file), 1600, 1200);
    }

    protected void showPlot(XYPlot plot, OutputMetricPlotter.PlotType outputType, String filename) throws IOException {
        String description = plot.getTitle().getText();
        switch (outputType) {
            case IN_FRAME:
                new PlotPanel(description).showInFrame(plot);
                break;
            case AS_FILE:
                this.save(plot, filename);
                break;
            case AS_FILE_AND_IN_FRAME:
                this.save(plot, filename);
                new PlotPanel(description).showInFrame(plot);
                break;
        }
    }

    @Override
    public GralDataContainer createDataContainer(int numDimensions, String label) {
        return new GralDataContainer(new DataTable(numDimensions, Double.class));
    }
}

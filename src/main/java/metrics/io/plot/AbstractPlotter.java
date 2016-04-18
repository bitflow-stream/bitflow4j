package metrics.io.plot;

import de.erichseifert.gral.io.plots.DrawableWriter;
import de.erichseifert.gral.io.plots.DrawableWriterFactory;
import de.erichseifert.gral.plots.XYPlot;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by anton on 4/18/16.
 */
public abstract class AbstractPlotter implements Plotter {

    private int[] color = {0, 0, 0};

    @Override
    public abstract String toString();

    public Color getNextColor() {
        this.color[0] = (color[0] + 32) % 256;
        this.color[1] = (color[1] + 128) % 256;
        this.color[2] = (color[2] + 64) % 256;

        return new Color(color[0], color[1], color[2], 128);
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

}

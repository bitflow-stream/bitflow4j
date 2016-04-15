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
 * plotpanel based on gral example
 */
public abstract class PlotPanel extends JPanel {

    private int[] color = {0, 0, 0};
    /**
     * Version id for serialization.
     */
    private static final long serialVersionUID = 8221256658243821951L;

    /**
     * Performs basic initialization of an example,
     * like setting a default size.
     */
    public PlotPanel() {
        super(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
    }

    /**
     * Returns a short title for the example.
     *
     * @return A title text.
     */
    public abstract String getTitle();

    /**
     * Returns a more detailed description of the example contents.
     *
     * @return A description of the example.
     */
    public abstract String getDescription();

    /**
     * Opens a frame and shows the example in it.
     *
     * @return the frame instance used for displaying the example.
     */
    protected JFrame showInFrame() {
        JFrame frame = new JFrame(getTitle());
        frame.getContentPane().add(this, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(getPreferredSize());
        frame.setVisible(true);
        return frame;
    }

    protected void save(XYPlot plot, String filename) {

        JFileChooser chooser = new JFileChooser();
        File file = null;
        int option = chooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            if (filename == null) {
                file = chooser.getSelectedFile();
            } else {
                file = new File(filename);
            }
            try {
                DrawableWriter writer = DrawableWriterFactory.getInstance().get("application/postscript");
                writer.write(plot, new FileOutputStream(file), 800, 600);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public String toString() {
        return getTitle();
    }


    public Color getNextColor() {
        this.color[0] = (color[0] + 32) % 256;
        this.color[1] = (color[1] + 128) % 256;
        this.color[2] = (color[2] + 64) % 256;

        Color rc = new Color(color[0], color[1], color[2], 128);

        return rc;
    }

    protected void decideOutput(XYPlot plot, OutputMetricPlotter.PlotType outputType, String filename) {
        switch (outputType) {
            case IN_FRAME:
                this.showInFrame();
                break;
            case AS_FILE:
                System.err.println("Save plot to file");
                this.save(plot, filename);
                break;
            case AS_FILE_AND_IN_FRAME:
                this.save(plot, filename);
                this.showInFrame();
                break;
        }
    }
}

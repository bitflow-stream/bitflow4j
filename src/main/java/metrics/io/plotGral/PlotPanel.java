package metrics.io.plotGral;

import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.ui.InteractivePanel;

import javax.swing.*;
import java.awt.*;

/**
 * plotpanel based on gral example
 */
public class PlotPanel extends JPanel {

    /**
     * Version id for serialization.
     */
    private static final long serialVersionUID = 8221256658243821951L;

    private final String title;

    /**
     * Performs basic initialization of an example,
     * like setting a default size.
     */
    public PlotPanel(String title) {
        super(new BorderLayout());
        this.title = title;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
    }

    /**
     * Returns a short title for the example.
     *
     * @return A title text.
     */
    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    /**
     * Opens a frame and shows the example in it.
     *
     * @return the frame instance used for displaying the example.
     */
    protected JFrame showInFrame(XYPlot plot) {
        InteractivePanel panel = new InteractivePanel(plot);
        panel.setPannable(true);
        panel.setZoomable(true);
        add(panel);
        JFrame frame = new JFrame(getTitle());
        frame.getContentPane().add(this, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(getPreferredSize());
        frame.setVisible(true);
        return frame;
    }

}

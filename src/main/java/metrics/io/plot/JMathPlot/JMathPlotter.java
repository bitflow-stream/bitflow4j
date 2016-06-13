package metrics.io.plot.JMathPlot;

import metrics.io.plot.AbstractPlotter;
import metrics.io.plot.ColorGenerator;
import metrics.io.plot.OutputMetricPlotter;
import org.math.plot.FrameView;
import org.math.plot.Plot2DPanel;
import org.math.plot.plots.ScatterPlot;
import org.math.plot.render.AbstractDrawer;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Map;

/**
 * Created by anton on 5/14/16.
 */
public class JMathPlotter extends AbstractPlotter<ArrayDataContainer> {

    @Override
    public void plotResult(OutputMetricPlotter.PlotType outputType, Map<String, ArrayDataContainer> map, String filename) throws IOException {
        if (outputType != OutputMetricPlotter.PlotType.IN_FRAME) {
            throw new UnsupportedOperationException("JMathPlotter only supports rendering in frames");
        }

        Plot2DPanel plot = new Plot2DPanel();
        plot.plotCanvas.setNotable(true);
        plot.plotCanvas.setNoteCoords(true);

        ColorGenerator colors = new ColorGenerator();
        for (Map.Entry<String, ArrayDataContainer> features : map.entrySet()) {
            double[][] input = features.getValue().toMatrix();
            double[][] plotvalues = new double[input.length][2];
            for (int i = 0; i < input.length; i++) {
                System.arraycopy(input[i], 0, plotvalues[i], 0, 2);
            }

            plot.addPlot(new ScatterPlot(features.getKey(), colors.getNextColor(),
                    AbstractDrawer.ROUND_DOT, 5, plotvalues));
        }
        FrameView plotframe = new FrameView(plot);
        plotframe.setVisible(true);
        waitForClose(plotframe);
    }

    private void waitForClose(JFrame frame) {
        Object lock = new Object();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                synchronized (lock) {
                    frame.setVisible(false);
                    lock.notify();
                }
            }
        });

        synchronized (lock) {
            while (frame.isVisible())
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public String toString() {
        return "jmath plotter";
    }

    @Override
    public ArrayDataContainer createDataContainer(int numDimensions, String label) {
        return new ArrayDataContainer(label);
    }

}

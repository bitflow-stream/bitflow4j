package metrics.io.plot;

import java.awt.*;
import java.io.IOException;
import java.util.Map;

/**
 * Created by anton on 4/18/16.
 */
public abstract class AbstractPlotter<T extends DataContainer> {

    private int[] color = {0, 0, 0};

    @Override
    public abstract String toString();

    public abstract void plotResult(OutputMetricPlotter.PlotType outputType, Map<String, T> map, String filename) throws IOException;

    public abstract T createDataContainer(int numDimensions);

    public Color getNextColor() {
        this.color[0] = (color[0] + 32) % 256;
        this.color[1] = (color[1] + 128) % 256;
        this.color[2] = (color[2] + 64) % 256;

        return new Color(color[0], color[1], color[2], 128);
    }

}

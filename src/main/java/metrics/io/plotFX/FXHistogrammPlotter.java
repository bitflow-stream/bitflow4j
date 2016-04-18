package metrics.io.plotFX;

import de.erichseifert.gral.data.DataTable;
import metrics.io.plotGral.AbstractPlotter;
import metrics.io.plotGral.OutputMetricPlotter;

import java.io.IOException;
import java.util.Map;

/**
 * Created by mwall on 18.04.16.
 */
public class FXHistogrammPlotter extends AbstractPlotter {
    @Override
    public String toString() {
        return null;
    }

    @Override
    public void plotResult(OutputMetricPlotter.PlotType outputType, Map<String, DataTable> map, String filename) throws IOException {

    }
}

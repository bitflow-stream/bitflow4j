package metrics.io.plot;

import de.erichseifert.gral.data.DataTable;

import java.io.IOException;
import java.util.Map;

/**
 * Created by mwall on 14.04.16.
 */
public interface Plotter {

    void plotResult(OutputMetricPlotter.PlotType outputType, Map<String, DataTable> map, String filename) throws IOException;

}

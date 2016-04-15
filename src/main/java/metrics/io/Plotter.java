package metrics.io;

import de.erichseifert.gral.data.DataTable;

import java.util.Map;

/**
 * Created by mwall on 14.04.16.
 */
public interface Plotter {

    void plotResult(int outputType, Map<String, DataTable> map, String filename);

}

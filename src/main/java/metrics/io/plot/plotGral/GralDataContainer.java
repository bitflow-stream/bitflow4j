package metrics.io.plot.plotGral;

import de.erichseifert.gral.data.DataTable;
import metrics.io.plot.DataContainer;

/**
 * Created by mwall on 18.04.16.
 */
public class GralDataContainer implements DataContainer {

    public final DataTable table;

    public GralDataContainer(DataTable table) {
        this.table = table;
    }

    @Override
    public void add(double... data) {
        Double[] values = new Double[data.length];
        for (int i = 0; i < data.length; i++)
            values[i] = data[i];
        this.table.add(values);
    }
}

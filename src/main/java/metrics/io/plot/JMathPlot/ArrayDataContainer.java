package metrics.io.plot.JMathPlot;

import metrics.io.plot.DataContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anton on 5/14/16.
 */
public class ArrayDataContainer implements DataContainer {

    public final List<double[]> data = new ArrayList<>();
    public final String label;

    public ArrayDataContainer(String label) {
        this.label = label;
    }

    @Override
    public void add(double... values) {
        data.add(values);
    }

    public double[][] toMatrix() {
        double[][] result = new double[data.size()][];
        for (int i = 0; i < result.length; i++)
            result[i] = data.get(i);
        return result;
    }

}

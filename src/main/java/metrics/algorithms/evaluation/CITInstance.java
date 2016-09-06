package metrics.algorithms.evaluation;

import weka.core.DenseInstance;
import weka.core.Instance;

/**
 * Created by malcolmx on 30.08.16.
 */
public class CITInstance extends DenseInstance{
    private static volatile int count = 0;

    private int index;

    public CITInstance(Instance instance) {
        super(instance);
        this.index = count++;
    }

    public CITInstance(double weight, double[] attValues) {
        super(weight, attValues);
        this.index = count++;
    }

    public CITInstance(int numAttributes) {
        super(numAttributes);
        this.index = count++;
    }

    public int getIndex(){
        return this.index;
    }

    public static void resetCounter() {
        count = 0;
    }
}

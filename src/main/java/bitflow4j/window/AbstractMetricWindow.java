package bitflow4j.window;

/**
 * @author fschmidt
 */
public abstract class AbstractMetricWindow {

    public final String name;

    public AbstractMetricWindow(String name) {
        this.name = name;
    }

    public abstract double[] getVector();

    public abstract void add(double val);

    public abstract void clear();
}

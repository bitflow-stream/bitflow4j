package metrics.main.data;

/**
 * Created by anton on 4/29/16.
 */
public class Host {

    public final String layer;
    public final String name;

    public Host(String name, String layer) {
        this.layer = layer;
        this.name = name;
    }

    public String toString() {
        return layer + "_host_" + name;
    }

}

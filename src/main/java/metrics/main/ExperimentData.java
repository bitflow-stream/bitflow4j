package metrics.main;

import java.io.IOException;
import java.util.List;

/**
 * Created by anton on 4/17/16.
 */
public abstract class ExperimentData {

    public abstract List<Host> getAllHosts();

    public abstract AppBuilder makeBuilder(Host host) throws IOException;

    public abstract String toString();

    public static class Host {

        public final String layer;
        public final String name;

        public Host(String name, String layer) {
            this.layer = layer;
            this.name = name;
        }

        public String toString() {
            return layer + " host " + name;
        }

    }
}

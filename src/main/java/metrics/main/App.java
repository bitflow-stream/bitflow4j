package metrics.main;

import java.io.IOException;

/**
 * Created by anton on 4/14/16.
 */
public interface App {

    void runAll(AppBuilder sourceDataBuilder) throws IOException;

}

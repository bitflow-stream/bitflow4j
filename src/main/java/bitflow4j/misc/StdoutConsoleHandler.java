package bitflow4j.misc;

import java.util.logging.ConsoleHandler;

/**
 * Created by anton on 14.02.17.
 */
public class StdoutConsoleHandler extends ConsoleHandler {

    public StdoutConsoleHandler() {
        super();
        setOutputStream(System.out);
    }

}

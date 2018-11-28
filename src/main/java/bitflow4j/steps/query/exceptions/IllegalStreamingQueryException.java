package bitflow4j.steps.query.exceptions;

import java.io.IOException;

/**
 * Abstract class for exceptions with a problem in the query.
 */
public abstract class IllegalStreamingQueryException extends IOException {

    public IllegalStreamingQueryException(String query) {
        super("Parsing Error when processing: " + query);
    }

}

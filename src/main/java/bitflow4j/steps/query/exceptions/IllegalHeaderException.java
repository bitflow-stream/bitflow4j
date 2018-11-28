package bitflow4j.steps.query.exceptions;

import java.io.IOException;

/**
 * Abstract class for exceptions where is a problem with sample header.
 */
public abstract class IllegalHeaderException extends IOException {

    public IllegalHeaderException(String query) {
        super("Parsing Error when processing: " + query);
    }

}
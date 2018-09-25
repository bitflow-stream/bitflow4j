package bitflow4j.main.script.endpoints;

/**
 * EndpointParseException represents an exception that occurred during parsing of an endpoint token and creation of a
 * corresponding Source/Sink
 */
public class EndpointParseException extends RuntimeException {
    /**
     * Constructs a new EndpointParseException
     * @param inputToken the inputToken that caused the error
     * @param message the actual error message
     */
    public EndpointParseException(String inputToken, String message) {
        super("Could not create Endpoint from \"" + inputToken + "\", " + message);
    }
}

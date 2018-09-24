package bitflow4j.main.script.endpoints;

public class EndpointParseException extends RuntimeException {
    public EndpointParseException(String input) {
        super("Could not create Endpoint from \"" + input + "\"");
    }

    public EndpointParseException(String input, String message) {
        super("Could not create Endpoint from \"" + input + "\", " + message);
    }

//    public EndpointParseException(String input, String message, Throwable cause) {
//        super("Could not create Endpoint from \"" + input + "\", " + message, cause);
//    }
}

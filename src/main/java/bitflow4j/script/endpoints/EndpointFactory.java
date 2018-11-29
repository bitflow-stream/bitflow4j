package bitflow4j.script.endpoints;

import bitflow4j.PipelineStep;
import bitflow4j.Source;
import bitflow4j.io.console.SampleReader;
import bitflow4j.io.console.SampleWriter;
import bitflow4j.io.file.FileSink;
import bitflow4j.io.file.FileSource;
import bitflow4j.io.marshall.*;
import bitflow4j.io.net.TcpListenerSource;
import bitflow4j.io.net.TcpSink;
import bitflow4j.io.net.TcpSource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * EndpointFactory provides methods to create Source and Sink from a endpoint token (script-like written form of an endpoint)
 */
public class EndpointFactory {

    private static boolean isValidPort(String input) {
        try {
            extractPort(input);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    private static boolean isValidHostAndPort(String input) {
        try {
            return !"".equals(extractHostPart(input)) && extractPort(input) > 0;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private static String extractHostPart(String tcpEndpoint) throws MalformedURLException {
        URL url = new URL("http://" + tcpEndpoint); // Exception when the tcp endpoint format is wrong.
        return url.getHost();
    }

    private static int extractPort(String tcpEndpoint) throws MalformedURLException {
        URL url = new URL("http://" + tcpEndpoint); // Exception when the tcp endpoint format is wrong.
        return url.getPort();
    }

    private static boolean isValidFilename(String file) {
        File f = new File(file);
        try {
            f.getCanonicalPath();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Creates a Source from one or multiple endpoint tokens.
     * Multiinput is only supported by TCP and File type.
     * The expected format is format+transport://target (e.g. csv+tcp://0.0.0.0:8080
     * Other formats are accepted and a best guess is used to determine format and transport.
     *
     * @param endpointTokens the tokens specifiying the input endpoint
     * @return the Source created from the tokens
     */
    public Source createSource(String... endpointTokens) throws IOException {
        List<Endpoint> endpoints = Arrays.stream(endpointTokens).map(this::parseEndpointToken).collect(Collectors.toList());
        Endpoint.Format format = endpoints.get(0).getFormat();
        Endpoint.Type type = endpoints.get(0).getType();
        for (Endpoint endpoint : endpoints) {
            if (endpoint.getFormat() != format || endpoint.getType() != type) {
                throw new EndpointParseException(Arrays.toString(endpointTokens), "Multiinput with varying formats or types.");
            }
        }
        Marshaller marshaller = getMarshaller(endpoints.get(0));
        switch (type) {
            case TCP:
                String[] targets = endpoints.stream().map(Endpoint::getTarget).toArray(String[]::new);
                return new TcpSource(targets, marshaller);
            case LISTEN:
                if (endpoints.size() > 1) {
                    throw new EndpointParseException(Arrays.toString(endpointTokens), "Multiinput of type LISTEN is not allowed");
                }
                return new TcpListenerSource(extractPort(endpoints.get(0).getTarget()), marshaller);
            case FILE:
                FileSource fs = new FileSource(marshaller);
                for (Endpoint endpoint : endpoints) {
                    fs.addFile(endpoint.getTarget());
                }
                return fs;
            case STD:
                return new SampleReader(marshaller);
            default:
                throw new EndpointParseException(Arrays.toString(endpointTokens), "Could not find an appropriate Sink for type " + type);
        }
    }

    /**
     * Creates a sink from a specify endpoint token.
     * The expected format is format+transport://target (e.g. csv+tcp://0.0.0.0:8080
     * Other formats are accepted and a best guess is used to determine format and transport.
     *
     * @param endpointToken the tokens specifying the output endpoint
     * @return the Sink created form the token
     */
    public PipelineStep createSink(String endpointToken) throws IOException {
        Endpoint endpoint = parseEndpointToken(endpointToken);
        Marshaller marshaller = getMarshaller(endpoint);
        switch (endpoint.getType()) {
            case TCP:
                return new TcpSink(marshaller, endpoint.getTarget());
            case FILE:
                return new FileSink(endpoint.getTarget(), marshaller);
            case STD:
                return new SampleWriter(marshaller);
            default:
                throw new EndpointParseException(endpointToken, "Could not find an appropriate Sink for type " + endpoint.getType());
        }
    }

    private Marshaller getMarshaller(Endpoint e) {
        Marshaller marshaller;
        switch (e.getFormat()) {
            case BINARY:
                marshaller = new BinaryMarshaller();
                break;
            case CSV:
                marshaller = new CsvMarshaller();
                break;
            case WAV:
                marshaller = new WavAudioMarshaller();
                break;
            case TEXT:
                marshaller = new TextMarshaller();
                break;
            default:
                throw new EndpointParseException(e.toString(), "Could not find a Marshaller for specified format " + e.getFormat());
        }
        return marshaller;
    }

    /**
     * ParseEndpointDescription parses the given string to an EndpointDescription object.
     * The string can be one of two forms: the URL-style description will be parsed by
     * ParseUrlEndpointDescription, other descriptions will be parsed by GuessEndpointDescription.
     *
     * @param endpointToken the full endpoint token
     */
    public Endpoint parseEndpointToken(String endpointToken) {
        if (endpointToken.contains("://")) {
            return parseURLEndpoint(endpointToken);
        } else {
            Endpoint endpoint = new Endpoint(endpointToken);
            endpoint.setTarget(endpointToken);
            endpoint.setType(guessEndpointType(endpointToken, endpointToken));
            endpoint.setFormat(guessFormat(endpoint));
            return endpoint;
        }
    }

    /**
     * ParseUrlEndpointDescription parses the endpoint string as a URL endpoint description.
     * It has the form:
     * format+transport://target
     * <p>
     * One of the format and transport parts must be specified, optionally both.
     * If one of format or transport is missing, it will be guessed.
     * The order does not matter. The 'target' part must not be empty.
     *
     * @param input the full endpointToken, expected to be in the URLFormat
     **/
    private Endpoint parseURLEndpoint(String input) {
        Endpoint result = new Endpoint(input);
        String[] urlParts = input.split("://");
        if (urlParts.length != 2 || "".equals(urlParts[0]) || "".equals(urlParts[1])) {
            throw new EndpointParseException(input, "URL expected to be in form of: format+transport://target");
        }
        result.setTarget(urlParts[1]);

        boolean formatProcessed = false;
        for (String part : urlParts[0].split("\\+")) {
            if (Endpoint.Format.find(part) != null) {
                if (formatProcessed) {
                    throw new EndpointParseException(input, "multiple formats defined for endpoint");
                }
                formatProcessed = true;
                Endpoint.Format f = Endpoint.Format.find(part);
                result.setFormat(f);
            } else if (Endpoint.Type.find(part) != null) {
                if (result.getType() != null) {
                    throw new EndpointParseException(input, "multiple types defined for endpoint");
                }
                result.setType(Endpoint.Type.find(part));
            } else {
                throw new EndpointParseException(input, "Unknown format or type: " + part);
            }
        }
        if (result.getType() == null) {
            Endpoint.Type guess = guessEndpointType(result.getTarget(), input);
            result.setType(guess);
        }
        if (result.getFormat() == null) {
            result.setFormat(guessFormat(result));
        }

        return result;
    }

    private Endpoint.Format guessFormat(Endpoint endpoint) {
        switch (endpoint.getType()) {
            case TCP:
            case LISTEN:
                return Endpoint.Format.BINARY;
            case FILE:
                if (endpoint.getTarget() != null && endpoint.getTarget().endsWith(".bin")) {
                    return Endpoint.Format.BINARY;
                } else if (endpoint.getTarget() != null && endpoint.getTarget().endsWith(".wav")) {
                    return Endpoint.Format.WAV;
                } else {
                    return Endpoint.Format.CSV;
                }
            case STD:
                return Endpoint.Format.TEXT;
        }
        return Endpoint.Format.UNDEFINED;
    }

    /**
     * GuessEndpointType guesses the EndpointType for the given target.
     * Three forms of are recognized for the target:
     * - A host:port pair indicates an active TCP endpoint
     * - A :port pair (without the host part, but with the colon) indicates a passive TCP endpoint listening on the given port.
     * - The hyphen '-' is interpreted as standard input/output.
     * - All other targets are treated as file names.
     *
     * @param target        the target to be used to guess the type
     * @param endpointToken the full endpointToken for improved Error message
     **/
    private Endpoint.Type guessEndpointType(String target, String endpointToken) {
        if (target == null || target.isEmpty()) {
            throw new EndpointParseException(endpointToken, "please provide a target");
        }
        if ("-".equals(target)) {
            return Endpoint.Type.STD;
        } else if (target.startsWith(":") && isValidPort(target)) {
            return Endpoint.Type.LISTEN;
        } else if (target.contains(":") && isValidHostAndPort(target)) {
            return Endpoint.Type.TCP;
        } else if (isValidFilename(endpointToken)) {
            return Endpoint.Type.FILE;
        } else {
            throw new EndpointParseException(endpointToken, "failed to guess target type");
        }
    }
}
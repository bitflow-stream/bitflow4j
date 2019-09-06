package bitflow4j.script.endpoints;

import bitflow4j.PipelineStep;
import bitflow4j.Source;
import bitflow4j.io.console.SampleReader;
import bitflow4j.io.console.SampleWriter;
import bitflow4j.io.file.FileSink;
import bitflow4j.io.file.FileSource;
import bitflow4j.io.marshall.Marshaller;
import bitflow4j.io.net.TcpListenerSource;
import bitflow4j.io.net.TcpSink;
import bitflow4j.io.net.TcpSource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * EndpointFactory provides methods to create Source and Sink from a endpoint token (script-like written form of an endpoint)
 */
public class EndpointFactory {

    private final Map<String, Function<String, Source>> customSources = new HashMap<>();

    public void registerCustomSource(String name, Function<String, Source> sourceSupplier) {
        customSources.put(name, sourceSupplier);
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
        Marshaller marshaller = null;
        if (type != Endpoint.Type.CUSTOM)
            marshaller = endpoints.get(0).getMarshaller();
        switch (type) {
            case TCP:
                String[] targets = endpoints.stream().map(Endpoint::getTarget).toArray(String[]::new);
                return new TcpSource(targets, marshaller);
            case LISTEN:
                if (endpoints.size() > 1) {
                    throw new EndpointParseException(Arrays.toString(endpointTokens), "Multiinput of type LISTEN is not allowed");
                }
                return new TcpListenerSource(TcpSink.getPort(endpoints.get(0).getTarget()), marshaller);
            case FILE:
                FileSource fs = new FileSource(marshaller);
                for (Endpoint endpoint : endpoints) {
                    fs.addFile(endpoint.getTarget());
                }
                return fs;
            case STD:
                return new SampleReader(marshaller);
            case CUSTOM:
                if (endpoints.size() > 1) {
                    throw new EndpointParseException(Arrays.toString(endpointTokens), "Multiinput with custom input type is not allowed");
                }
                String customType = endpoints.get(0).getCustomType();
                if (customSources.containsKey(customType)) {
                    return customSources.get(customType).apply(endpoints.get(0).getTarget());
                }
                // Fallthrough to the default case, if the custom type is not defined
            default:
                throw new EndpointParseException(Arrays.toString(endpointTokens), "Could not find an appropriate Source for type " + type);
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
        Marshaller marshaller = endpoint.getMarshaller();
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

    /**
     * ParseEndpointDescription parses the given string to an EndpointDescription object.
     * The string can be one of two forms: the URL-style description will be parsed by
     * ParseUrlEndpointDescription, other descriptions will be parsed by GuessEndpointDescription.
     *
     * @param endpointToken the full endpoint token
     */
    public Endpoint parseEndpointToken(String endpointToken) {
        if (endpointToken == null || endpointToken.isEmpty()) {
            throw new EndpointParseException(endpointToken, "Endpoint cannot be empty");
        }
        if (endpointToken.contains("://")) {
            return parseURLEndpoint(endpointToken);
        } else {
            Endpoint.Type type = guessEndpointType(endpointToken);
            assert type != null;
            Endpoint.Format format = guessFormat(type, endpointToken);
            return new Endpoint(endpointToken, endpointToken, format, type);
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
    public Endpoint parseURLEndpoint(String input) {
        String[] urlParts = input.split("://");
        if (urlParts.length != 2 || urlParts[0].isEmpty() || urlParts[1].isEmpty()) {
            throw new EndpointParseException(input, "URL expected to be in form of: format+transport://target");
        }
        String target = urlParts[1];
        Endpoint.Format format = null;
        Endpoint.Type type = null;
        String customType = "";

        for (String part : urlParts[0].split("\\+")) {
            if (Endpoint.Format.find(part) != null) {
                if (format != null) {
                    throw new EndpointParseException(input, "multiple formats defined for endpoint");
                }
                format = Endpoint.Format.find(part);
            } else if (Endpoint.Type.find(part) != null) {
                if (type != null) {
                    throw new EndpointParseException(input, "multiple types defined for endpoint");
                }
                type = Endpoint.Type.find(part);
            } else if (customSources.containsKey(part)) {
                type = Endpoint.Type.CUSTOM;
                customType = part;
            } else {
                throw new EndpointParseException(input, "Unknown format or type: " + part);
            }
        }
        if (type == Endpoint.Type.STD && !"-".equals(target)) {
            throw new EndpointParseException(input, "Type 'std' requires target '-', target was " + target);
        }
        if (type == null) {
            type = guessEndpointType(target);
            assert type != null;
        }
        if (format == null) {
            format = guessFormat(type, target);
        }
        return new Endpoint(input, target, format, type, customType);
    }

    public static Endpoint.Format guessFormat(Endpoint.Type type, String target) {
        switch (type) {
            case TCP:
            case LISTEN:
                return Endpoint.Format.BIN;
            case FILE:
                if (target.endsWith(".bin")) {
                    return Endpoint.Format.BIN;
                } else if (target.endsWith(".wav")) {
                    return Endpoint.Format.WAV;
                } else {
                    return Endpoint.Format.CSV;
                }
            case STD:
                return Endpoint.Format.CSV;
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
     * @param target the target to be used to guess the type
     **/
    public static Endpoint.Type guessEndpointType(String target) {
        if ("-".equals(target)) {
            return Endpoint.Type.STD;
        } else if (target.startsWith(":") && isValidPort(target.substring(1))) {
            return Endpoint.Type.LISTEN;
        } else if (target.contains(":") && isValidHostAndPort(target)) {
            return Endpoint.Type.TCP;
        } else if (isValidFilename(target)) {
            return Endpoint.Type.FILE;
        }
        throw new EndpointParseException(target, "failed to guess target type");
    }

    public static boolean isValidPort(String portString) {
        try {
            return TcpSink.getPort("host:" + portString) > 0;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static boolean isValidHostAndPort(String input) {
        try {
            return !"".equals(TcpSink.getHost(input)) && TcpSink.getPort(input) > 0;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static boolean isValidFilename(String file) {
        File f = new File(file);
        try {
            return !f.getCanonicalPath().isEmpty();
        } catch (IOException e) {
            return false;
        }
    }

}

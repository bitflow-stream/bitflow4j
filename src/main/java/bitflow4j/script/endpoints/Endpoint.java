package bitflow4j.script.endpoints;

import bitflow4j.io.marshall.*;

/**
 * Endpoint contains the parsed information of an endpoint token
 */
public class Endpoint {

    private final String endpointToken;
    private final Format format;
    private final Type type;
    private final String target;

    public Endpoint(String endpointToken, String target, Format format, Type type) {
        this.endpointToken = endpointToken;
        this.target = target;
        this.type = type;
        this.format = format;
    }

    public Format getFormat() {
        return format;
    }

    public Type getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return endpointToken;
    }

    public Marshaller getMarshaller() {
        Marshaller marshaller;
        switch (getFormat()) {
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
                throw new EndpointParseException(toString(), "Could not find a Marshaller for specified format " + getFormat());
        }
        return marshaller;
    }

    public enum Type {
        TCP, LISTEN, FILE, STD, EMPTY;

        public static Type find(String search) {
            for (Type t : Type.values()) {
                if (t.name().compareToIgnoreCase(search) == 0) {
                    return t;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum Format {
        UNDEFINED, TEXT, CSV, BINARY, WAV;

        public static Format find(String search) {
            for (Format t : Format.values()) {
                if (t.name().compareToIgnoreCase(search) == 0) {
                    return t;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

}

package bitflow4j;

import bitflow4j.script.endpoints.EndpointFactory;

import java.util.function.Consumer;

/**
 * Implementing this class can be used to register a custom sample source in the Main class.
 * For this, the implementation of CustomSourceFactory must be provided as command line parameter:
 * <p>
 * --endpoint-plugin package.of.CustomFactoryImplementation
 * <p>
 * The implementation of the createSource() method should return an implementation of the abstract CustomSource class.
 *
 * @author kevinstyp
 */
public abstract class CustomSourceFactory implements Consumer<EndpointFactory> {

    public final String sourceName;

    public CustomSourceFactory(String sourceName) {
        this.sourceName = sourceName;
    }

    public abstract Source createSource(String parameter);

    @Override
    public void accept(EndpointFactory factory) {
        factory.registerCustomSource(sourceName, this::createSource);
    }

}

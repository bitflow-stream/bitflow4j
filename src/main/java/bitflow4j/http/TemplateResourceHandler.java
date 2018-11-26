package bitflow4j.http;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

/**
 * Created by anton on 06.01.17.
 */
public abstract class TemplateResourceHandler {

    private final TemplateEngine engine;

    public TemplateResourceHandler(Server server, String path) {
        server.addRoute(path, ResourceHandler.class, this);
        engine = createEngine();
    }

    private TemplateEngine createEngine() {
        TemplateEngine engine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver(getClass().getClassLoader());
        resolver.setName("classloader-" + getClass().getName());
        resolver.setCacheable(true);
        resolver.setTemplateMode(TemplateMode.HTML);
        engine.setTemplateResolver(resolver);
        return engine;
    }

    public abstract String getPath(UriResource uri, Map<String, String> urlParams, IHTTPSession session);

    public abstract void fillContext(Context context, Map<String, String> urlParams, IHTTPSession session) throws Handler.HandlerException;

    public static class ResourceHandler extends Handler {

        public ResourceHandler() {
            super(Method.GET);
        }

        @Override
        public Response handle_uri(UriResource uri, Map<String, String> urlParams, IHTTPSession session) throws HandlerException {
            TemplateResourceHandler parent = uri.initParameter(TemplateResourceHandler.class);
            String resource = parent.getPath(uri, urlParams, session);

            Context context = new Context();
            parent.fillContext(context, urlParams, session);
            StringWriter writer = new StringWriter(1024);
            parent.engine.process(resource, context, writer);

            InputStream data = new ByteArrayInputStream(writer.toString().getBytes());
            return NanoHTTPD.newChunkedResponse(Status.OK, StaticResourceHandler.getMimeType(resource), data);
        }

    }

}

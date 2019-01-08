package bitflow4j.steps.output;

import bitflow4j.Pipeline;
import bitflow4j.Sample;
import bitflow4j.io.file.FileSink;
import bitflow4j.io.marshall.Marshaller;
import bitflow4j.misc.Pair;
import bitflow4j.script.endpoints.Endpoint;
import bitflow4j.script.endpoints.EndpointFactory;
import bitflow4j.steps.fork.Distributor;
import bitflow4j.steps.fork.Fork;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileTemplateOutput extends Fork {

    private static final Logger logger = Logger.getLogger(FileTemplateOutput.class.getName());

    private final String fileNameTemplate;

    public FileTemplateOutput(String fileNameTemplate) {
        this(fileNameTemplate, EndpointFactory.guessFormat(Endpoint.Type.FILE, fileNameTemplate).getMarshaller());
    }

    public FileTemplateOutput(String fileNameTemplate, String format) {
        this(fileNameTemplate, Marshaller.get(format));
    }

    public FileTemplateOutput(String fileNameTemplate, Marshaller marshaller) {
        super(new MultiFileDistributor(fileNameTemplate, marshaller));
        this.fileNameTemplate = fileNameTemplate;
    }

    @Override
    public String toString() {
        return String.format("Output to files named from template: %s", fileNameTemplate);
    }

    private static class MultiFileDistributor implements Distributor {

        private final Map<String, Collection<Pair<String, Pipeline>>> outputs = new HashMap<>();
        private final String fileNameTemplate;
        private final Marshaller marshaller;

        private MultiFileDistributor(String fileNameTemplate, Marshaller marshaller) {
            this.fileNameTemplate = fileNameTemplate;
            this.marshaller = marshaller;
        }

        @Override
        public Collection<Pair<String, Pipeline>> distribute(Sample sample) {
            String fileName = sample.resolveTagTemplate(fileNameTemplate);
            if (outputs.containsKey(fileName)) {
                return outputs.get(fileName);
            } else {
                Pipeline pipe = new Pipeline();
                try {
                    pipe.step(new FileSink(fileName, marshaller));
                } catch (IOException e) {
                    logger.log(Level.SEVERE, String.format("Failed to create file output '%s' (resolved from template %s)", fileName, fileNameTemplate), e);
                    return Collections.emptyList();
                }
                Collection<Pair<String, Pipeline>> result = Collections.singleton(new Pair<>(fileName, pipe));
                outputs.put(fileName, result);
                return result;
            }
        }

    }

}

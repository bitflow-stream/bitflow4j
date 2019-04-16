package bitflow4j.io.file;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Header;
import bitflow4j.Sample;
import bitflow4j.io.MarshallingSampleWriter;
import bitflow4j.io.marshall.BinaryMarshaller;
import bitflow4j.io.marshall.CsvMarshaller;
import bitflow4j.io.marshall.Marshaller;
import bitflow4j.io.marshall.TextMarshaller;
import bitflow4j.script.registry.Description;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kevinstyp on 15/04/2019.
 */
@Description("Uses the provided template in parameter file to save samples in their respective file. Example call: " +
        "output-files(file='./${data_type}/${serial}.csv', marshaller='CSV') " +
        "This will evaluate the tags data_type and serial of every sample and save the sample in the proper file.")
public class OutputFiles extends AbstractPipelineStep {

    private static final Logger logger = Logger.getLogger(OutputFiles.class.getName());

    private final Marshaller marshaller;
    protected OutputStream output = null;
    private final Header.ChangeChecker header = new Header.ChangeChecker();

    private final boolean append;
    private int index = 0;
    private String baseFileTemplate;

    private Map<String, File> valueFiles = new HashMap<String, File>();

    public OutputFiles(String file) throws IOException {
        this(file, determineMarshaller(file));
    }

    public OutputFiles(String file, String marshaller) throws IOException {
        this(file, Marshaller.get(marshaller));
    }

    public OutputFiles(String file, Marshaller marshaller) throws IOException {
        this(file, marshaller, false);
    }

    public OutputFiles(String baseFileTemplate, Marshaller marshaller, boolean append) throws IOException {
        this.marshaller = marshaller;
        this.baseFileTemplate = baseFileTemplate;
        this.append = append;
    }

    public Marshaller getMarshaller() {
        return marshaller;
    }

    @Override
    public synchronized void writeSample(Sample sample) throws IOException {
        OutputStream output = this.output; // Avoid race condition
        try {
            if (header.changed(sample.getHeader()) || output == null) {
                OutputStream newOutput = nextOutputStream(sample);

                // Use object identity to determine if the output stream has changed. If so, close the old one.
                if (newOutput != output) {
                    closeStream();
                    this.output = newOutput;
                    output = newOutput;
                }
                if (output == null)
                    return;
                marshaller.marshallHeader(output, sample.getHeader());
                output.flush();
            }
            marshaller.marshallSample(output, sample);
        } catch (IOException e) {
            closeStream();
            throw e;
        }
        super.writeSample(sample);
    }

    protected void closeStream() {
        OutputStream output = this.output; // Avoid race condition
        if (output != null) {
            this.output = null;
            try {
                output.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to close output stream", e);
            }
        }
    }

    @Override
    public void doClose() {
        closeStream();
    }


    @Override
    public String toString() {
        return String.format("Writing files with template '%s' (append: %s, format: %s)", baseFileTemplate, append, getMarshaller());
    }

    protected OutputStream nextOutputStream(Sample sample) throws IOException {
        if (output != null && append) {
            // Do not create new files for new headers
            return output;
        }

        String value = sample.resolveTagTemplate(baseFileTemplate);
        if (valueFiles.get(value) != null) {
            // Take the file form the 'cache' (has been written to previously already)
            return new BufferedOutputStream(new FileOutputStream(valueFiles.get(value), true));
        }
        else {
            index = 0;
            File file;
            FileGroup files = new FileGroup(value);
            // Create new file following the behaviour of FileGroup and remember it, if similar samples occur
            do {
                file = new File(files.getFile(index++));
            } while (!append && file.exists());

            if (!file.exists() && !file.createNewFile()) {
                throw new IOException("Failed to create file " + file);
            }
            logger.fine(String.format("Opening output file for writing (append: %s)", append));

            valueFiles.put(value, file);

            return new BufferedOutputStream(new FileOutputStream(file, append));
        }
    }

    private static Marshaller determineMarshaller(String fileName) {
        if (fileName.endsWith(".bin")) {
            return new BinaryMarshaller();
        } else if (fileName.endsWith(".text")) {
            return new TextMarshaller();
        } else {
            return new CsvMarshaller();
        }
    }

}

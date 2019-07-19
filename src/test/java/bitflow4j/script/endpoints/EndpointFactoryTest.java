package bitflow4j.script.endpoints;

import bitflow4j.PipelineStep;
import bitflow4j.Source;
import bitflow4j.io.file.FileSink;
import bitflow4j.io.file.FileSource;
import bitflow4j.io.marshall.BinaryMarshaller;
import bitflow4j.io.marshall.CsvMarshaller;
import bitflow4j.io.marshall.WavAudioMarshaller;
import bitflow4j.io.net.TcpListenerSource;
import bitflow4j.io.net.TcpSink;
import bitflow4j.io.net.TcpSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.*;

public class EndpointFactoryTest {

    private String testFileA = "testfile_A";
    private String testFileB = "testfile_B";

    @BeforeEach
    public void reset() throws IOException {
        Files.write(Paths.get(testFileA), new byte[]{});
        Files.write(Paths.get(testFileB), new byte[]{});
    }

    @AfterEach
    public void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get(testFileA));
        Files.deleteIfExists(Paths.get(testFileB));
    }

    @Test
    public void givenFileEndpoints_whenCreateSink_thenReturnFileSink() throws IOException {
        String fileEndpoint = "file+wav://" + testFileA;

        PipelineStep sink = new EndpointFactory().createSink(fileEndpoint);

        assertTrue(sink instanceof FileSink);
        assertTrue(((FileSink) sink).getMarshaller() instanceof WavAudioMarshaller);
    }

    @Test
    public void givenTCPEndpoints_whenCreateSink_thenReturnTCPSink() throws IOException {
        String fileEndpoint = "tcp+bin://0.1.2.3:8012";

        PipelineStep sink = new EndpointFactory().createSink(fileEndpoint);

        assertTrue(sink instanceof TcpSink);
        assertTrue(((TcpSink) sink).getMarshaller() instanceof BinaryMarshaller);
    }

    @Test
    public void givenMixedFormatSources_whenCreateSource_thenThrowException() throws IOException {
        String[] mixedSources = new String[]{"tcp+csv://some.url", "tcp+text://some.url"};

        Throwable t = assertThrows(EndpointParseException.class,
                () -> new EndpointFactory().createSource(mixedSources));
        assertThat(t.getMessage(), containsString("Multiinput with varying formats or type"));
    }

    @Test
    public void givenMixedTypeSources_whenCreateSource_thenThrowException() throws IOException {
        String[] mixedSources = new String[]{"tcp+csv://some.url", "file+csv://some.file"};

        Throwable t = assertThrows(EndpointParseException.class,
                () -> new EndpointFactory().createSource(mixedSources));
        assertThat(t.getMessage(), containsString("Multiinput with varying formats or type"));
    }

    @Test
    public void givenMultipleLISTENSources_whenCreateSource_thenThrowException() throws IOException {
        String[] mixedSources = new String[]{"listen+csv://some.url", "listen+csv://some.file"};

        Throwable t = assertThrows(EndpointParseException.class,
                () -> new EndpointFactory().createSource(mixedSources));
        assertThat(t.getMessage(), containsString(" not allowed"));
    }

    @Test
    public void givenMultifileEndpoints_whenCreateSource_thenReturnFileSource() throws IOException {
        String[] multiFileSource = new String[]{"file+csv://" + testFileA, "file+csv://" + testFileB};

        Source source = new EndpointFactory().createSource(multiFileSource);

        assertThat(source, instanceOf(FileSource.class));
        assertEquals(((FileSource) source).getFiles().get(0).getName(), testFileA);
        assertEquals(((FileSource) source).getFiles().get(1).getName(), testFileB);
        assertThat(((FileSource) source).getMarshaller(), instanceOf(CsvMarshaller.class));
    }

    @Test
    public void givenFileEndpoints_whenCreateSource_thenReturnFileSource() throws IOException {
        String[] fileEndpoint = new String[]{"file+wav://" + testFileA};

        Source source = new EndpointFactory().createSource(fileEndpoint);
        assertThat(source, instanceOf(FileSource.class));
        assertEquals(((FileSource) source).getFiles().get(0).getName(), testFileA);
        assertThat(((FileSource) source).getMarshaller(), instanceOf(WavAudioMarshaller.class));
    }

    @Test
    public void givenTCPEndpoints_whenCreateSource_thenReturnTCPSource() throws IOException {
        String[] tcpEndpoint = new String[]{"tcp+wav://1.2.3.4:8083"};

        Source source = new EndpointFactory().createSource(tcpEndpoint);
        assertThat(source, instanceOf(TcpSource.class));
        assertThat(source.toString(), containsString("1.2.3.4:8083"));
    }

    @Test
    public void givenTCPListenEndpoints_whenCreateSource_thenReturnTCPListenerSource() throws IOException {
        String[] tcpListenEndpoint = new String[]{"listen+text://1.2.3.4:8083"};
        Source source = new EndpointFactory().createSource(tcpListenEndpoint);
        assertThat(source, instanceOf(TcpListenerSource.class));
    }

    private void executeTest(String endpointToken, Endpoint.Type expectedType, Endpoint.Format expectedFormat, String expectedTarget) {
        Endpoint res = new EndpointFactory().parseEndpointToken(endpointToken);
        assertEquals(expectedFormat, res.getFormat(), "format not as expected for endpointToken " + endpointToken);
        assertEquals(expectedType, res.getType(), "type not as expected for endpointToken " + endpointToken);
        assertEquals(expectedTarget, res.getTarget(), "target not as expected for endpointToken " + endpointToken);
    }

    private void executeExceptionTest(String endpointToken, String errorMessage) {
        Throwable t = assertThrows(EndpointParseException.class,
                () -> new EndpointFactory().parseEndpointToken(endpointToken));
        assertThat(t.getMessage(), containsString(errorMessage));
    }

    @Test
    public void testEndpointParser() {
        String sampleHostPort = "some.host.com:8080";
        executeTest("tcp+csv://" + sampleHostPort, Endpoint.Type.TCP, Endpoint.Format.CSV, sampleHostPort);
        // standard format for std is text
        executeTest("std://-", Endpoint.Type.STD, Endpoint.Format.CSV, "-");
        // standard format for tcp is bin
        executeTest("tcp://" + sampleHostPort, Endpoint.Type.TCP, Endpoint.Format.BIN, sampleHostPort);
        // standard format for file with .bin ending is binary
        executeTest("file://filename.bin", Endpoint.Type.FILE, Endpoint.Format.BIN, "filename.bin");
        executeTest("file://filename.csv", Endpoint.Type.FILE, Endpoint.Format.CSV, "filename.csv");
        // standard format for other files is binary
        executeTest("file://filename", Endpoint.Type.FILE, Endpoint.Format.CSV, "filename");

        // GUESSES
        // guess host and port as tcp
        executeTest(sampleHostPort, Endpoint.Type.TCP, Endpoint.Format.BIN, sampleHostPort);
        // guess port as tcp listen
        executeTest(":8000", Endpoint.Type.LISTEN, Endpoint.Format.BIN, ":8000");
        // guess file
        executeTest("/opt/somefile", Endpoint.Type.FILE, Endpoint.Format.CSV, "/opt/somefile");
        // guess CSV file
        executeTest("/opt/somefile.bin", Endpoint.Type.FILE, Endpoint.Format.BIN, "/opt/somefile.bin");
        // guess wav file
        executeTest("/opt/somefile.wav", Endpoint.Type.FILE, Endpoint.Format.WAV, "/opt/somefile.wav");
        // guess console
        executeTest("-", Endpoint.Type.STD, Endpoint.Format.CSV, "-");
    }

    @Test
    public void testEndpointParserException() {
        executeExceptionTest("unknown_type+csv://asd", "Unknown format or type");
        executeExceptionTest("unknown_format+file://asd", "Unknown format or type");
        executeExceptionTest("file+unknown_format://asd", "Unknown format or type");
        executeExceptionTest("file+unknown_format://", "URL expected to be in form of: format+transport://target");
        executeExceptionTest("://asdasd", "URL expected to be in form of: format+transport://target");
        executeExceptionTest("", "Endpoint cannot be empty");
        executeExceptionTest(null, "Endpoint cannot be empty");
    }

}

package bitflow4j.main.script.endpoints;

import bitflow4j.io.file.FileSink;
import bitflow4j.io.file.FileSource;
import bitflow4j.io.marshall.BinaryMarshaller;
import bitflow4j.io.marshall.CsvMarshaller;
import bitflow4j.io.marshall.WavAudioMarshaller;
import bitflow4j.io.net.TcpListenerSource;
import bitflow4j.io.net.TcpSink;
import bitflow4j.io.net.TcpSource;
import bitflow4j.main.script.generated.BitflowParser;
import bitflow4j.sample.Sink;
import bitflow4j.sample.Source;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EndpointFactoryTest {
    private String testFileA = "testfile_A";
    private String testFileB = "testfile_B";


    @Before
    public void reset() throws IOException {
        Files.write(Paths.get(testFileA), new byte[]{});
        Files.write(Paths.get(testFileB), new byte[]{});
    }

    @After
    public void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get(testFileA));
        Files.deleteIfExists(Paths.get(testFileB));
    }


    @Test
    public void givenFileEndpoints_whenCreateSink_thenReturnFileSink() throws IOException {
        String fileEndpoint = "file+wav://" + testFileA;

        Sink sink = new EndpointFactory().createSink(fileEndpoint);

        assertTrue(sink instanceof FileSink);
        assertTrue(((FileSink) sink).getMarshaller() instanceof WavAudioMarshaller);
    }

    @Test
    public void givenTCPEndpoints_whenCreateSink_thenReturnTCPSink() throws IOException {
        String fileEndpoint = "tcp+binary://0.1.2.3:8012";

        Sink sink = new EndpointFactory().createSink(fileEndpoint);

        assertTrue(sink instanceof TcpSink);
        assertTrue(((TcpSink) sink).getMarshaller() instanceof BinaryMarshaller);
    }

    @Test(expected = EndpointParseException.class)
    public void givenMixedFormatSources_whenCreateSource_thenThrowException() throws IOException {
        String[] mixedSources = new String[]{"tcp+csv://some.url", "tcp+text://some.url"};

        try {
            new EndpointFactory().createSource(mixedSources);

        } catch (EndpointParseException e) {
            assertContains(e.getMessage(), "Multiinput with varying formats or type");
            throw e;
        }
    }

    @Test(expected = EndpointParseException.class)
    public void givenMixedTypeSources_whenCreateSource_thenThrowException() throws IOException {
        String[] mixedSources = new String[]{"tcp+csv://some.url", "file+csv://some.file"};

        try {
            new EndpointFactory().createSource(mixedSources);

        } catch (EndpointParseException e) {
            assertContains(e.getMessage(), "Multiinput with varying formats or type");
            throw e;
        }
    }

    @Test(expected = EndpointParseException.class)
    public void givenMultipleLISTENSources_whenCreateSource_thenThrowException() throws IOException {
        String[] mixedSources = new String[]{"listen+csv://some.url", "listen+csv://some.file"};

        try {
            new EndpointFactory().createSource(mixedSources);

        } catch (EndpointParseException e) {
            assertContains(e.getMessage(), " not allowed");
            throw e;
        }
    }

    @Test
    public void givenMultifileEndpoints_whenCreateSource_thenReturnFileSource() throws IOException {
        String[] multiFileSource = new String[]{"file+csv://" + testFileA, "file+csv://" + testFileB};

        Source source = new EndpointFactory().createSource(multiFileSource);

        assertTrue(source instanceof FileSource);
        assertEquals(((FileSource) source).getFiles().get(0).getName(), testFileA);
        assertEquals(((FileSource) source).getFiles().get(1).getName(), testFileB);
        assertTrue(((FileSource) source).getMarshaller() instanceof CsvMarshaller);
    }

    @Test
    public void givenFileEndpoints_whenCreateSource_thenReturnFileSource() throws IOException {
        String[] fileEndpoint = new String[]{"file+wav://" + testFileA};

        Source source = new EndpointFactory().createSource(fileEndpoint);

        assertTrue(source instanceof FileSource);
        assertEquals(((FileSource) source).getFiles().get(0).getName(), testFileA);
        assertTrue(((FileSource) source).getMarshaller() instanceof WavAudioMarshaller);
    }

    @Test
    public void givenTCPEndpoints_whenCreateSource_thenReturnTCPSource() throws IOException {
        String[] tcpEndpoint = new String[]{"tcp+wav://1.2.3.4:8083"};

        Source source = new EndpointFactory().createSource(tcpEndpoint);

        assertTrue(source instanceof TcpSource);
        assertTrue(source.toString().contains("1.2.3.4:8083"));
    }

    @Test
    public void givenTCPListenEndpoints_whenCreateSource_thenReturnTCPListenerSource() throws IOException {
        String[] tcpListenEndpoint = new String[]{"listen+text://1.2.3.4:8083"};

        Source source = new EndpointFactory().createSource(tcpListenEndpoint);

        assertTrue(source instanceof TcpListenerSource);
    }

    @Test
    public void testEndpointParser() {
        String sampleHostPort = "some.host.com:8080";
        executeTest("tcp+csv://" + sampleHostPort, Endpoint.Type.TCP, Endpoint.Format.CSV, sampleHostPort);
        // standard format for std is text
        executeTest("std://-", Endpoint.Type.STD, Endpoint.Format.TEXT, "-");
        // standard format for tcp is bin
        executeTest("tcp://" + sampleHostPort, Endpoint.Type.TCP, Endpoint.Format.BINARY, sampleHostPort);
        // standard format for file with .bin ending is binary
        executeTest("file://filename.bin", Endpoint.Type.FILE, Endpoint.Format.BINARY, "filename.bin");
        // standard format for other files is csv
        executeTest("file://filename", Endpoint.Type.FILE, Endpoint.Format.CSV, "filename");

        // GUESSES
        // guess host and port as tcp
        executeTest(sampleHostPort, Endpoint.Type.TCP, Endpoint.Format.BINARY, sampleHostPort);
        // guess port as tcp listen
        executeTest(":8000", Endpoint.Type.LISTEN, Endpoint.Format.BINARY, ":8000");
        // guess file
        executeTest("/opt/somefile", Endpoint.Type.FILE, Endpoint.Format.CSV, "/opt/somefile");
        // guess wav file
        executeTest("/opt/somefile.wav", Endpoint.Type.FILE, Endpoint.Format.WAV, "/opt/somefile.wav");
        // guess console
        executeTest("-", Endpoint.Type.STD, Endpoint.Format.TEXT, "-");
    }

    @Test
    public void testEndpointParserException() {
        executeExceptionTest("unknown_type+csv://asd", "Unknown format or type");
        executeExceptionTest("unknown_format+file://asd", "Unknown format or type");
        executeExceptionTest("file+unknown_format://asd", "Unknown format or type");
        executeExceptionTest("file+unknown_format://", "URL expected to be in form of: format+transport://target");
        executeExceptionTest("://asdasd", "URL expected to be in form of: format+transport://target");
        executeExceptionTest("", "please provide a target");
    }

    private void executeTest(String endpointToken, Endpoint.Type expectedType, Endpoint.Format expectedFormat, String expectedTarget) {
        Endpoint res = new EndpointFactory().parseEndpointToken(endpointToken);
        assertEquals("format not as expected for endpointToken " + endpointToken, expectedFormat, res.getFormat());
        assertEquals("type not as expected for endpointToken " + endpointToken, expectedType, res.getType());
        assertEquals("target not as expected for endpointToken " + endpointToken, expectedTarget, res.getTarget());
    }

    private void executeExceptionTest(String endpointToken, String errorMessage) {
        try {
            new EndpointFactory().parseEndpointToken(endpointToken);
        } catch (EndpointParseException e) {
            assertContains(e.getMessage(), errorMessage);
            return;
        }
        Assert.fail("Expected EndpointParseException to be thrown, but no exception was thrown for endpointToken \"" + endpointToken + "\"");
    }

    private void assertContains(String fullMessage, String subMessage) {
        if (!fullMessage.contains(subMessage)) {
            Assert.fail("\n\tExpected to contain:\t\t" + subMessage + "\n\tActual message:\t\t\t\t" + fullMessage);
        }
    }

}

package bitflow4j.io.marshall;

import bitflow4j.Header;
import bitflow4j.Sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Based on: https://thiscouldbebetter.wordpress.com/2011/08/14/reading-and-writing-a-wav-file-in-java/ and:
 * http://www.labbookpages.co.uk/audio/javaWavFiles.html
 *
 * @author fschmidt
 */
public class WavAudioMarshaller implements Marshaller {

    public WavAudioMarshaller() {

    }

    @Override
    public boolean peekIsHeader(InputStream input) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static final String WAV_TAG_CHUNK_SIZE = "wavTagChunkSize";
    public static final String WAV_TAG_FMT = "wavTagFMT";
    public static final String WAV_TAG_CHUNK_SIZE_IN_BYTES = "wavTagChunkSizeInBytes";
    public static final String WAV_TAG_FORMAT_CODE = "wavTagformatCode";
    public static final String WAV_TAG_NUM_CHANNELS = "wavTagNumChannels";
    public static final String WAV_TAG_SAMPLES_PER_SECOND = "wavTagSamplesPerSecond";
    public static final String WAV_TAG_BYTES_PER_SECOND = "wavTagBytesPerSecond";
    public static final String WAV_TAG_BYTES_PER_SAMPLE_MAYBE = "wavTagBytesPerSampleMaybe";
    public static final String WAV_TAG_BITS_PER_SAMPLE = "wavTagBitsPerSample";
    public static final String WAV_TAG_SUBCHUNK2SIZE = "wavTagSubchunk2Size";

    private Short numberOfChannels;
    private int samplesPerSecond;
    private Short bitsPerSample;
    private Date timestamp;
    private Map<String, String> tags;

    @Override
    public UnmarshalledHeader unmarshallHeader(InputStream input) throws IOException {
        timestamp = new Date();
        int riffTag = readInt(input);
        if (riffTag != 0x52494646) { // "RIFF"
            throw new IOException("Wav file is missing RIFF indicator.");
        }
        int chunkSize = readInt(input);
        //CHECK WAV TAG
        int waveTag = readInt(input);
        if (waveTag != 0x57415645) // "WAVE"
        {
            throw new IOException("Wav file is missing WAVE indicator.");
        }
        int fmt = readInt(input);
        int chunkSizeInBytes = readInt(input);
        Short formatCode = readShort(input);

        numberOfChannels = readShort(input);
        samplesPerSecond = readInt(input);
        int bytesPerSecond = readInt(input);
        Short bytesPerSampleMaybe = readShort(input);
        bitsPerSample = readShort(input);
        int subchunk2Size = readInt(input);

        tags = new HashMap<>();
        tags.put(WAV_TAG_CHUNK_SIZE, Integer.toString(chunkSize));
        tags.put(WAV_TAG_FMT, Integer.toString(fmt));
        tags.put(WAV_TAG_CHUNK_SIZE_IN_BYTES, Integer.toString(chunkSizeInBytes));
        tags.put(WAV_TAG_FORMAT_CODE, Short.toString(formatCode));
        tags.put(WAV_TAG_NUM_CHANNELS, Short.toString(numberOfChannels));
        tags.put(WAV_TAG_SAMPLES_PER_SECOND, Integer.toString(samplesPerSecond));
        tags.put(WAV_TAG_BYTES_PER_SECOND, Integer.toString(bytesPerSecond));
        tags.put(WAV_TAG_BYTES_PER_SAMPLE_MAYBE, Short.toString(bytesPerSampleMaybe));
        tags.put(WAV_TAG_BITS_PER_SAMPLE, Short.toString(bitsPerSample));
        tags.put(WAV_TAG_SUBCHUNK2SIZE, Integer.toString(subchunk2Size));

        String[] headerNames = new String[numberOfChannels];
        for (int i = 0; i < numberOfChannels; i++) {
            headerNames[i] = "channel-" + (i + 1);
        }
        Header header = new Header(headerNames);
        return new UnmarshalledHeader(header, true);
    }

    @Override
    public Sample unmarshallSample(InputStream input, UnmarshalledHeader header) throws IOException {
        final int bitsPerByte = 8;
        int bytesPerSample = bitsPerSample / bitsPerByte;
        byte[] nextSample = new byte[bytesPerSample];
        long sampleDurationInMs = (long) (1000 / (1.0 * samplesPerSecond));
        timestamp.setTime(timestamp.getTime() + sampleDurationInMs);
        double[] metricValues = new double[numberOfChannels];

        for (int c = 0; c < numberOfChannels; c++) {
            input.read(nextSample);
            if (null != bitsPerSample) {
                switch (bitsPerSample) {
                    case 16:
                        metricValues[c] = buildFromBytes16(nextSample);
                        break;
                    case 24:
                        metricValues[c] = buildFromBytes24(nextSample);
                        break;
                    case 32:
                        metricValues[c] = buildFromBytes32(nextSample);
                        break;
                    default:
                        throw new IOException("Not valid number of bits per sample.");
                }
            }
        }
        return new Sample(header.header, metricValues, timestamp, tags);
    }

    @Override
    public void marshallHeader(OutputStream output, Header header) throws IOException {
    }

    private boolean insertedHeader = false;

    private Short bitsPerSampleWrite;

    @Override
    public void marshallSample(OutputStream output, Sample sample) throws IOException {
        if (!insertedHeader) {
            int chunkSize = Integer.parseInt(sample.getTag(WAV_TAG_CHUNK_SIZE));
            int fmt = Integer.parseInt(sample.getTag(WAV_TAG_FMT));
            int chunkSizeInBytes = Integer.parseInt(sample.getTag(WAV_TAG_CHUNK_SIZE_IN_BYTES));
            Short formatCode = Short.parseShort(sample.getTag(WAV_TAG_FORMAT_CODE));
            Short numberOfChannels = Short.parseShort(sample.getTag(WAV_TAG_NUM_CHANNELS));
            int samplesPerSecond = Integer.parseInt(sample.getTag(WAV_TAG_SAMPLES_PER_SECOND));
            int bytesPerSecond = Integer.parseInt(sample.getTag(WAV_TAG_BYTES_PER_SECOND));
            Short bytesPerSampleMaybe = Short.parseShort(sample.getTag(WAV_TAG_BYTES_PER_SAMPLE_MAYBE));
            Short bitsPerSample = Short.parseShort(sample.getTag(WAV_TAG_BITS_PER_SAMPLE));
            int subchunk2Size = Integer.parseInt(sample.getTag(WAV_TAG_SUBCHUNK2SIZE));
            bitsPerSampleWrite = bitsPerSample;
            byte[] riff = "RIFF".getBytes(StandardCharsets.UTF_8);
            output.write(riff);
            writeInt(output, chunkSize);
            byte[] wave = "WAVE".getBytes(StandardCharsets.UTF_8);
            output.write(wave);
            writeInt(output, fmt);
            writeInt(output, chunkSizeInBytes);
            writeShort(output, formatCode);
            writeShort(output, numberOfChannels);
            writeInt(output, samplesPerSecond);
            writeInt(output, bytesPerSecond);
            writeShort(output, bytesPerSampleMaybe);
            writeShort(output, bitsPerSample);
            writeInt(output, subchunk2Size);
            insertedHeader = true;
        }

        for (int c = 0; c < sample.getMetrics().length; c++) {
            double value = sample.getMetrics()[c];
            if (null != bitsPerSampleWrite) {
                switch (bitsPerSampleWrite) {
                    case 16:
                        writeDouble16(output, value);
                        break;
                    case 24:
                        writeDouble24(output, value);
                        break;
                    case 32:
                        writeDouble32(output, value);
                        break;
                    default:
                        throw new IOException("Not valid number of bits per sample.");
                }
            }
        }
    }

    private double buildFromBytes16(byte[] valueAsBytes) {
        Short valueAsShort = (short) (((valueAsBytes[0] & 0xFF))
                | (short) ((valueAsBytes[1] & 0xFF) << 8));

        return valueAsShort.doubleValue();
    }

    private double buildFromBytes24(byte[] valueAsBytes) {
        Short valueAsShort = (short) (((valueAsBytes[0] & 0xFF))
                | ((valueAsBytes[1] & 0xFF) << 8)
                | ((valueAsBytes[2] & 0xFF) << 16));

        return valueAsShort.doubleValue();
    }

    private double buildFromBytes32(byte[] valueAsBytes) {
        Short valueAsShort = (short) (((valueAsBytes[0] & 0xFF))
                | ((valueAsBytes[1] & 0xFF) << 8)
                | ((valueAsBytes[2] & 0xFF) << 16)
                | ((valueAsBytes[3] & 0xFF) << 24));

        return valueAsShort.doubleValue();
    }

    private void writeDouble16(OutputStream output, double value) throws IOException {
        short v = toShort(value);
        byte[] b = new byte[]{
                (byte) ((v) & 0xFF),
                (byte) ((v >>> 8) & 0xFF)
        };
        output.write(b);
    }

    private void writeDouble24(OutputStream output, double value) throws IOException {
        int v = toInt(value);
        byte[] b = new byte[]{
                (byte) ((v) & 0xFF),
                (byte) ((v >>> 8) & 0xFF),
                (byte) ((v >>> 16) & 0xFF)};
        output.write(b);
    }

    private void writeDouble32(OutputStream output, double value) throws IOException {
        int v = toInt(value);
        byte[] b = new byte[]{
                (byte) ((v) & 0xFF),
                (byte) ((v >>> 8) & 0xFF),
                (byte) ((v >>> 16) & 0xFF),
                (byte) ((v >>> 24) & 0xFF),};
        output.write(b);
    }

    private int readInt(InputStream input) throws IOException {
        byte[] size4Array = new byte[4];
        input.read(size4Array);
        long result = ((size4Array[0] & 0xFF)
                | ((size4Array[1] & 0xFF) << 8)
                | ((size4Array[2] & 0xFF) << 16)
                | ((size4Array[3] & 0xFF) << 24));
        return (int) result;
    }

    private short readShort(InputStream input) throws IOException {
        byte[] bytesLittleEndian = new byte[2];
        input.read(bytesLittleEndian);
        int returnValueAsInt = ((bytesLittleEndian[0] & 0xFF)
                | ((bytesLittleEndian[1] & 0xFF) << 8));
        return (short) returnValueAsInt;
    }

    private void writeInt(OutputStream output, int intToWrite) throws IOException {
        byte[] intToWriteAsBytesLittleEndian = new byte[]{
                (byte) (intToWrite & 0xFF),
                (byte) ((intToWrite >> 8) & 0xFF),
                (byte) ((intToWrite >> 16) & 0xFF),
                (byte) ((intToWrite >> 24) & 0xFF),};
        output.write(intToWriteAsBytesLittleEndian, 0, 4);
    }

    private void writeShort(OutputStream output, short shortToWrite) throws IOException {
        byte[] shortToWriteAsBytesLittleEndian = new byte[]{
                (byte) shortToWrite,
                (byte) (shortToWrite >>> 8 & 0xFF),};
        output.write(shortToWriteAsBytesLittleEndian, 0, 2);
    }

    private short toShort(double value) {
        if (value < Short.MIN_VALUE) {
            return Short.MIN_VALUE;
        }
        if (value > Short.MAX_VALUE) {
            return Short.MAX_VALUE;
        }
        return (short) value;
    }

    private int toInt(double value) {
        return (int) value;
    }

}

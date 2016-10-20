package bitflow4j.main;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Created by anton on 5/5/16.
 * <p>
 * Use a wrapped ByteArrayDataOutput to implement the ParameterHash interface.
 * For debugging purposes, printing all input
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ParameterHashImpl implements ParameterHash {

    private static final Logger logger = Logger.getLogger(ParameterHashImpl.class.getName());

    private final ByteArrayDataOutput output;
    private final PrintStream writer = System.err;
    private final boolean printInputs;

    public ParameterHashImpl() {
        this(false);
    }

    public ParameterHashImpl(boolean printInputs) {
        this(ByteStreams.newDataOutput(), printInputs);
    }

    public ParameterHashImpl(ByteArrayDataOutput output, boolean printInputs) {
        this.output = output;
        this.printInputs = printInputs;
    }

    private void print(String message) {
        if (printInputs)
            logger.info(message);
    }

    @Override
    public void write(int b) {
        print("Writing byte " + b);
        output.write(b);
    }

    @Override
    public void write(byte[] b) {
        print("Writing bytes " + Arrays.toString(b));
        output.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        print("Writing bytes " + Arrays.toString(b) + " off " + off + " len " + len);
        output.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) {
        print("Writing boolean " + v);
        output.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) {
        print("Writing byte " + v);
        output.writeByte(v);
    }

    @Override
    public void writeShort(int v) {
        print("Writing short " + v);
        output.writeShort(v);
    }

    @Override
    public void writeChar(int v) {
        print("Writing char " + v);
        output.writeChar(v);
    }

    @Override
    public void writeInt(int v) {
        print("Writing int " + v);
        output.writeInt(v);
    }

    @Override
    public void writeLong(long v) {
        print("Writing long " + v);
        output.writeLong(v);
    }

    @Override
    public void writeFloat(float v) {
        print("Writing float " + v);
        output.writeFloat(v);
    }

    @Override
    public void writeDouble(double v) {
        print("Writing double " + v);
        output.writeDouble(v);
    }

    @Override
    public void writeChars(String s) {
        print("Writing chars " + s);
        output.writeChars(s);
    }

    @Override
    public void writeUTF(String s) {
        print("Writing UTF " + s);
        output.writeUTF(s);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void writeBytes(String s) {
        print("Writing bytes (String) " + s);
        output.writeBytes(s);
    }

    @Override
    public byte[] toByteArray() {
        return output.toByteArray();
    }
}

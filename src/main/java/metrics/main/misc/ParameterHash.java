package metrics.main.misc;

import com.google.common.io.ByteArrayDataOutput;
import org.apache.commons.codec.binary.Base32;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by anton on 5/9/16.
 */
public interface ParameterHash extends ByteArrayDataOutput {

    default void writeClassName(Object obj) {
        // TODO HACK: Lambdas must be treated specially because their classname changes when recompiling
        String converterStr = obj.getClass().toString();
        int lambdaIndex = converterStr.indexOf("$Lambda$");
        if (lambdaIndex > 0) converterStr = converterStr.substring(0, lambdaIndex);
        writeChars(converterStr);
    }

    default String toFilename() {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] rawHash = digest.digest(toByteArray());
            byte[] name = new Base32().encode(rawHash);
            return new String(name).replace("=", "");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to get MD5 hash instance");
        }
    }

}

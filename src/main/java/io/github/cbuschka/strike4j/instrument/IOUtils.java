package io.github.cbuschka.strike4j.instrument;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class IOUtils {

    public static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        byte[] buf = new byte[1024 * 4];
        int count;
        while ((count = in.read(buf)) != -1) {
            bytesOut.write(buf, 0, count);
        }
        bytesOut.close();
        return bytesOut.toByteArray();
    }

    @SneakyThrows
    public static BigInteger getMd5For(byte[] data) {
        MessageDigest md5 = MessageDigest.getInstance("md5");
        byte[] checksum = md5.digest(data);
        return new BigInteger(checksum);
    }
}

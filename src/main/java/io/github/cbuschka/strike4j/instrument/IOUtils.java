package io.github.cbuschka.strike4j.instrument;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
}

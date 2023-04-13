package io.github.cbuschka.strike4j.instrument;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class StrikeDataOutputStream extends FilterOutputStream {

    public StrikeDataOutputStream(OutputStream out) {
        super(out);
    }

    public void writeUint16(short value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
    }

    public void writeUint32(int value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
        out.write((value >> 16) & 0xFF);
        out.write((value >> 24) & 0xFF);
    }

    public void writeUint8(int level) throws IOException {
        out.write(level);
    }

    public void writeSint8(int pan) throws IOException {
        out.write(pan);
    }

    public void writeBool8(boolean b) throws IOException {
        out.write(b ? 1 : 0);
    }

    public void writeZeroTerminatedString(String s) throws IOException {
        out.write(s.getBytes(StandardCharsets.UTF_8));
        out.write(0);
    }
}

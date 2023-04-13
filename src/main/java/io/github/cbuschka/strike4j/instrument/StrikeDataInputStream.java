package io.github.cbuschka.strike4j.instrument;

import java.io.*;
import java.util.Arrays;

class StrikeDataInputStream extends FilterInputStream {
    private int pos;

    StrikeDataInputStream(InputStream in) {
        this(in, 0);
    }

    private StrikeDataInputStream(InputStream in, int pos) {
        super(in);
        this.pos = pos;
    }

    public String readZeroTerminatedString() throws IOException {
        StringBuilder buf = new StringBuilder();
        int b;
        while (true) {
            b = read();
            if (b == -1) {
                if (buf.length() > 0) {
                    throw new EOFException("at pos " + pos);
                } else {
                    return null;
                }
            }
            if (b == 0) {
                return buf.toString();
            }

            buf.append((char) b);
        }
    }

    public void consumeBytes(byte[] expectedBytes) throws IOException {
        byte[] buf = readNBytes(expectedBytes.length);
        if (buf.length != expectedBytes.length || Arrays.compare(expectedBytes, buf) != 0) {
            throw new IOException("Expected " + toString(expectedBytes) + " , but was " + toString(buf) + " at pos " + pos + ".");
        }
    }

    public void skipNBytes(int n) throws IOException {
        readNBytes(n);
    }

    static String toString(byte[] bytes) {
        StringBuilder buf = new StringBuilder("{");
        for (int i = 0; i < bytes.length; ++i) {
            buf.append(i == 0 ? "" : ", ").append(Integer.toString(bytes[i], 16));
        }
        buf.append("}");
        return buf.toString();
    }

    public int readInt32() throws IOException {
        byte[] buffer = readNBytes(4);
        return (buffer[0] & 0xFF) | (buffer[1] & 0xFF) << 8 | (buffer[2] & 0xFF) << 16 | (buffer[3] & 0xFF) << 24;
    }

    public int readInt16() throws IOException {
        byte[] buffer = readNBytes(2);
        return (buffer[0] & 0xFF) | (buffer[1] & 0xFF) << 8;
    }

    public boolean readBool8() throws IOException {
        int x = readUint8();
        if (x == 0) {
            return false;
        } else if (x == 1) {
            return true;
        } else {
            throw new IOException("Expected bool8 at " + pos + " to be 0 or 1, but was 0x" + Integer.toHexString(x) + ".");
        }
    }

    public int readUint8() throws IOException {
        int x = read();
        if (x < 0) {
            throw new EOFException("at pos " + pos);
        }

        return x;
    }

    public byte readSint8() throws IOException {
        int x = read();
        if (x < 0) {
            throw new EOFException("at pos " + pos);
        }

        return (byte) x;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count = super.read(b, off, len);
        if (count != -1) {
            pos = pos + count;
        }
        return count;
    }

    @Override
    public int read() throws IOException {
        int c = super.read();
        if (c != -1) {
            pos++;
        }
        return c;
    }

    @Override
    public byte[] readNBytes(int len) throws IOException {
        byte[] bytes = super.readNBytes(len);
        if (bytes.length != len) {
            throw new EOFException("at pos " + pos);
        }
        return bytes;
    }

    public int getPos() {
        return pos;
    }

    public StrikeDataInputStream substream(int count) throws IOException {
        int pos = this.pos;
        byte[] buf = readNBytes(count);
        return new StrikeDataInputStream(new ByteArrayInputStream(buf), pos);
    }
}

package io.github.cbuschka.strike4j.instrument;

import com.sun.tools.javac.Main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InstrumentWriter {
    private StrikeDataOutputStream out;

    public InstrumentWriter(OutputStream out) {
        this.out = new StrikeDataOutputStream(out);
    }

    public void write(Instrument instrument) throws IOException {
        writeFileHeader();
        new MainSection(out).write(instrument);
        List<String> stringTable = new MappingsSection(out).write(instrument);
        new StringsSection(out).write(stringTable);
    }

    private void writeFileHeader() throws IOException {
        out.write("INST".getBytes(StandardCharsets.UTF_8));
    }

    public void close() throws IOException {
        this.out.close();
    }


    private static class MainSection {
        private StrikeDataOutputStream out;

        public MainSection(StrikeDataOutputStream out) {
            this.out = out;
        }

        public void write(Instrument instrument) throws IOException {
            out.writeUint32(24);
            out.write(0);
            int dontKnow3 = 0; // 0 or 3?
            out.write(dontKnow3);
            out.write(new byte[]{1, 0});
            out.write(new byte[]{0, 0});
            out.writeUint8(instrument.getLevel());
            out.writeSint8(instrument.getPan());
            out.writeUint8(instrument.getDecay());
            out.write(new byte[]{0, 0});
            out.writeSint8(instrument.getSemi());
            out.writeSint8(instrument.getFine());
            out.writeUint8(instrument.getCutOff());
            out.writeBool8(instrument.getFilterType() == FilterType.HIPASS);
            out.writeSint8(instrument.getVelDecay());
            out.writeSint8(instrument.getVelPitch());
            out.writeSint8(instrument.getVelFilter());
            out.writeSint8(instrument.getVelLevel());
            out.write(new byte[]{0, 0x7f});
            out.writeBool8(instrument.isLoopOn());
            out.write(new byte[]{0, 0});
        }
    }

    private static class MappingsSection {
        private static final int COMMAND = 0x63;

        private final StrikeDataOutputStream allOut;

        public MappingsSection(StrikeDataOutputStream out) {
            this.allOut = out;
        }

        public List<String> write(Instrument instrument) throws IOException {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            StrikeDataOutputStream out = new StrikeDataOutputStream(bytesOut);

            out.writeBool8(instrument.getCycleMode() == CycleMode.RANDOM);
            out.write(0); // skipped
            List<SampleMapping> sampleMappings = instrument.getSampleMappings();
            out.writeUint8(sampleMappings.size());
            out.write(0); // 0,11

            List<String> stringTable = new ArrayList<>();
            for (int i = 0; i < sampleMappings.size(); ++i) {
                SampleMapping sampleMapping = sampleMappings.get(i);
                stringTable.add(sampleMapping.getSamplePath());
                out.writeUint16((short) (stringTable.size() - 1));
                out.writeSint8(COMMAND);

                out.writeSint8(sampleMapping.getMinVelocity());
                out.writeSint8(sampleMapping.getMaxVelocity());
                out.write(new byte[]{0, 0x7f}); // 0,0x7f or  3c, 3c for Instruments/Crashes/ZilStacker ST.sin
                out.write(new byte[]{0, 0, 0});
                out.writeSint8(sampleMapping.getHihatOpenMin());
                out.writeSint8(sampleMapping.getHihatOpenMax());
                out.write(new byte[]{0, 0, 0, 0}); // in.consumeBytes(new byte[]{0, 0, 0, 0}); 0,4,0,0 or hhz1 edge
                out.write(new byte[]{0, 0});
                int dontKnow = 0;
                out.writeUint8(dontKnow);
                out.write(new byte[]{0});
                out.write(new byte[]{0, 0, 0, 0});
                out.write(new byte[]{0});
                int dontKnow2 = 1; // 1,3,7?
                out.writeUint8(dontKnow2);
                out.write(new byte[]{0, 0});
            }
            out.close();

            byte[] mappingSectionPayloadBytes = bytesOut.toByteArray();
            allOut.write(new byte[]{'m', 's', 'm', 'p'});
            allOut.writeUint32(mappingSectionPayloadBytes.length);
            allOut.write(mappingSectionPayloadBytes);

            return stringTable;
        }
    }

    private static class StringsSection {
        private StrikeDataOutputStream allOut;

        public StringsSection(StrikeDataOutputStream out) {
            this.allOut = out;
        }

        private void write(List<String> stringTable) throws IOException {

            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            StrikeDataOutputStream out = new StrikeDataOutputStream(bytesOut);

            for (String s : stringTable) {
                out.writeZeroTerminatedString(s);
            }
            out.close();

            byte[] stringTablePayloadBytes = bytesOut.toByteArray();
            allOut.write(new byte[]{'s', 't', 'r', ' '});
            allOut.writeUint32(stringTablePayloadBytes.length); // placeholder for strTableLen
            allOut.write(stringTablePayloadBytes);
        }

    }

}

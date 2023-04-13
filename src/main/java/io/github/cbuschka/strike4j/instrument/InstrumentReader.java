package io.github.cbuschka.strike4j.instrument;


import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
public class InstrumentReader implements AutoCloseable {
    String path;
    StrikeDataInputStream allIn;

    public InstrumentReader(String path, InputStream in) throws IOException {
        if (in == null) {
            throw new IllegalArgumentException("In must not be null.");
        }
        this.path = path;
        byte[] all = IOUtils.readAll(in);
        if ((all.length % 4) != 0) {
            throw new IOException("Length of file is no multiple of 4.");
        }
        this.allIn = new StrikeDataInputStream(new ByteArrayInputStream(all));
    }

    public Instrument read() throws IOException {
        return read(true);
    }

    public Instrument read(boolean validate) throws IOException {
        readFileHeader();

        Instrument instrument = new Instrument();
        instrument.setPath(path);
        MainSection mainSection = getMainSection();
        mainSection.read(instrument);

        MappingsSection mappingsSection = getMappingsSection();
        List<String> strings = getStringsSection().read();
        mappingsSection.read(instrument, strings);

        if (validate) {
            InstrumentValidator validator = new InstrumentValidator();
            Set<ConstraintViolation<Instrument>> violations = validator.validate(instrument);
            if (!violations.isEmpty()) {
                throw new IOException("Instrument is not valid: " + violations);
            }
        }

        return instrument;
    }

    @Override
    public void close() throws IOException {
        this.allIn.close();
    }

    private StringsSection getStringsSection() throws IOException {
        allIn.consumeBytes(new byte[]{'s', 't', 'r', ' '});
        int strSectionLen = allIn.readInt32();
        StrikeDataInputStream in = allIn.substream(strSectionLen);
        return new StringsSection(in, strSectionLen);
    }

    private MappingsSection getMappingsSection() throws IOException {
        allIn.consumeBytes(new byte[]{'m', 's', 'm', 'p'});
        int msmpLen = allIn.readInt32();
        StrikeDataInputStream in = allIn.substream(msmpLen);
        return new MappingsSection(in);
    }


    private void readFileHeader() throws IOException {
        allIn.consumeBytes("INST".getBytes(StandardCharsets.UTF_8)); // file type signature
    }

    private MainSection getMainSection() throws IOException {
        int headerLength = allIn.readInt32();  // header length "24 bytes"
        StrikeDataInputStream in = allIn.substream(headerLength);
        return new MainSection(in);
    }

    private static class MainSection {
        private final StrikeDataInputStream in;

        public MainSection(StrikeDataInputStream in) {
            this.in = in;
        }

        public void read(Instrument instrument) throws IOException {
            in.consumeBytes(new byte[]{0});
            int dontKnow3 = in.readUint8(); // 0 or 3?
            in.consumeBytes(new byte[]{1, 0});
            in.consumeBytes(new byte[]{0, 0});
            int level = in.readUint8();
            instrument.setLevel(level);
            int pan = in.readSint8();
            instrument.setPan(pan);
            int decay = in.readUint8();
            instrument.setDecay(decay);
            in.consumeBytes(new byte[]{0, 0});
            int semi = in.readSint8();
            instrument.setSemi(semi);
            int fine = in.readSint8();
            instrument.setFine(fine);
            int cutOff = in.readUint8();
            instrument.setCutOff(cutOff);
            boolean hipass = in.readBool8();
            FilterType filterType = hipass ? FilterType.HIPASS : FilterType.LOPASS;
            instrument.setFilterType(filterType);
            int velDecay = in.readSint8();
            instrument.setVelDecay(velDecay);
            int velPitch = in.readSint8();
            instrument.setVelPitch(velPitch);
            int velFilter = in.readSint8();
            instrument.setVelFilter(velFilter);
            int velLevel = in.readSint8();
            instrument.setVelLevel(velLevel);
            in.consumeBytes(new byte[]{0, 0x7f});
            boolean loopOn = in.readBool8();
            instrument.setLoopOn(loopOn);
            in.consumeBytes(new byte[]{0, 0});
        }
    }

    private static class MappingsSection {
        private static final int[] VALID_COMMANDS = {0x4d, 0x53, 0x54, 0x56, 0x57, 0x5a, 0x5c, 0x5d, 0x5e, 0x5f, 0x60, 0x61, 0x62, 0x63 /* 'c' */};

        private final StrikeDataInputStream in;

        public MappingsSection(StrikeDataInputStream in) {
            this.in = in;
        }

        public void read(Instrument instrument, List<String> strings) throws IOException {

            int cycleModeRr0Random1 = in.readSint8();
            CycleMode cycleMode = cycleModeRr0Random1 == 0 ? CycleMode.ROUND_ROBIN : CycleMode.RANDOM;
            instrument.setCycleMode(cycleMode);
            in.skipNBytes(1);
            int rageMappingCount = in.readUint8();
            in.skipNBytes(1); // 0, 11?

            if (rageMappingCount > 0) {
                for (int i = 0; i < rageMappingCount; ++i) {
                    int stringIndex = in.readInt16();
                    String string = strings.get(stringIndex);
                    int command = in.readUint8();
                    boolean isValidCommand = Arrays.stream(VALID_COMMANDS).anyMatch(x -> x == command);
                    if (isValidCommand) {
                        int velocityRangeMin = in.readUint8();
                        int velocityRangeMax = in.readUint8();
                        in.skipNBytes(2); // 0,0x7f or  3c, 3c for Instruments/Crashes/ZilStacker ST.sin
                        in.skipNBytes(3);
                        int hihatOpenRangeMin = in.readUint8();
                        int hihatOpenRangeMax = in.readUint8();
                        in.skipNBytes(4); // in.consumeBytes(new byte[]{0, 0, 0, 0}); 0,4,0,0 or hhz1 edge
                        in.skipNBytes(2);
                        int dontKnow = in.readUint8();
                        in.skipNBytes(1);
                        in.skipNBytes(4);
                        in.skipNBytes(1);
                        int dontKnow2 = in.readUint8(); // 1,3,7?
                        in.skipNBytes(2);
                        // log.info("path={} Got range {}-{} (hihatOpenedRangeMin={},hihatOpenedRangeMax={},dontKnow={},dontKnow2={}) string {}", path, min, max, hihatOpenedRangeMin, hihatOpenedRangeMax, dontKnow, dontKnow2, index);

                        SampleMapping sampleMapping = new SampleMapping(velocityRangeMin, velocityRangeMax, hihatOpenRangeMin, hihatOpenRangeMax, string);
                        instrument.getSampleMappings().add(sampleMapping);
                    } else {
                        throw new IOException("Unknown command 0x" + Integer.toHexString(command) + " at " + (in.getPos() - 1) + ".");
                    }

                }
            }
        }
    }

    private static class StringsSection {
        private final StrikeDataInputStream in;
        private final int strSectionLen;

        public StringsSection(StrikeDataInputStream in, int strSectionLen) {
            this.in = in;
            this.strSectionLen = strSectionLen;
        }

        private List<String> read() throws IOException {
            List<String> strings = new ArrayList<>();
            if (strSectionLen > 0) {
                while (true) {
                    String str = in.readZeroTerminatedString();
                    // log.info("Got {}", str != null ? str : "<null>");
                    if (str == null || str.isEmpty()) {
                        break;
                    }

                    strings.add(str);
                }
            }

            return strings;
        }

    }
}

package io.github.cbuschka.strike4j.instrument;


import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;

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
            int instrumentGroupNum = in.readUint8();
            instrument.setGroup(InstrumentGroup.valueOf(instrumentGroupNum));
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
            int undefined0 = in.readUint8();
            instrument.setUnknown0(undefined0);
            int rageMappingCount = in.readUint8();
            int undefined1 = in.readUint8(); // 0, 11?
            instrument.setUnknown1(undefined1);

            if (rageMappingCount > 0) {
                for (int i = 0; i < rageMappingCount; ++i) {
                    int stringIndex = in.readInt16();
                    String samplePath = strings.get(stringIndex);
                    int command = in.readUint8();
                    boolean isValidCommand = Arrays.stream(VALID_COMMANDS).anyMatch(x -> x == command);
                    if (isValidCommand) {
                        SampleMapping sampleMapping = new SampleMapping();
                        sampleMapping.setCommand(command);
                        sampleMapping.setSamplePath(samplePath);
                        int velocityRangeMin = in.readUint8();
                        sampleMapping.setMinVelocity(velocityRangeMin);
                        int velocityRangeMax = in.readUint8();
                        sampleMapping.setMaxVelocity(velocityRangeMax);
                        int undefined2 = in.readUint8(new int[]{0, 0x3c}); // 0 mostly, 3c for Instruments/Crashes/ZilStacker ST.sin
                        sampleMapping.setUnknown2(undefined2);
                        int undefined3 = in.readUint8(new int[]{0x7f, 0x3c}); // mostly 127
                        sampleMapping.setUnknown3(undefined3);
                        int undefined4 = in.readSint8(); // pos? bb snare -2 (OK!), mostly >0, 1, 2, 3, 4 ... max number of sample mappings (dups seen)
                        sampleMapping.setUnknown4(undefined4);
                        int undefined5 = in.readUint8(); // new int[]{0,0xc8,0x9c,0x70}
                        sampleMapping.setUnknown5(undefined5);
                        int undefined6 = in.readSint8(); // -1, -2, mostly 0, 1, 2, 3, 4 ...
                        sampleMapping.setUnknown6(undefined6);
                        int hihatOpenRangeMin = in.readUint8();
                        sampleMapping.setHihatOpenMin(hihatOpenRangeMin);
                        int hihatOpenRangeMax = in.readUint8();
                        sampleMapping.setHihatOpenMax(hihatOpenRangeMax);
                        int undefined7 = in.readUint8(new int[]{6, 4, 5, 0, 3});
                        sampleMapping.setUnknown7(undefined7);
                        int undefined8 = in.readUint8(new int[]{0, 6, 4, 3});
                        sampleMapping.setUnknown8(undefined8);
                        in.consumeBytes(new byte[]{0, 0, 0});
                        int undefined9 = in.readUint8(new int[]{0x0, 0x78, 0x1, 0x64, 0x7e, 0x6e});
                        sampleMapping.setUnknown9(undefined9);
                        int undefined10 = in.readUint8(); // 0x40,0x97,0xe3,0xa4, 0x34,0x98, 0-255?
                        sampleMapping.setUnknown10(undefined10);
                        int undefined11 = in.readUint8(); // mostly 0, 1-?
                        sampleMapping.setUnknown11(undefined11);
                        in.consumeBytes(new byte[]{0, 0, 0, 0});
                        int undefined12 = in.readUint8(new int[]{0x0, 0x3c});
                        sampleMapping.setUnknown12(undefined12);
                        int undefined13 = in.readUint8(new int[]{0, 1, 3, 5, 7});
                        sampleMapping.setUnknown13(undefined13);
                        in.consumeBytes(new byte[]{0, 0});
                        // log.info("path={} group={} u2={} u3={} u4={} y={} #={}", instrument.getPath(), instrument.getGroup(), undefined2, undefined3, undefined4, y, rageMappingCount);
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

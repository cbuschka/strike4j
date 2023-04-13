package io.github.cbuschka.strike4j.instrument;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Objects;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Slf4j
class InstrumentWriterTest {

    private final File backupZipFilePath = new File("strike-sdcard-bak.zip");

    @Test
    void canReadWriteAndReadAllFromDrumModule() throws IOException {
        assumeTrue(backupZipFilePath.isFile(), "SDCard backup required.");

        try (ZipFile zipFile = new ZipFile(backupZipFilePath);) {
            zipFile.stream()
                    .filter((zipFileEntry) -> zipFileEntry.getName().endsWith(".sin"))
                    .map((zipFileEntry) -> {
                        try {
                            InputStream in = zipFile.getInputStream(zipFileEntry);
                            InstrumentReader rd = new InstrumentReader(zipFileEntry.getName(),
                                    in);
                            Instrument instrumentRead = rd.read();

                            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
                            InstrumentWriter writer = new InstrumentWriter(bytesOut);
                            writer.write(instrumentRead);
                            writer.close();
                            Instrument instrumentReread = new InstrumentReader(zipFileEntry.getName(), new ByteArrayInputStream(bytesOut.toByteArray())).read();

                            InstrumentAssertions.assertEqual(instrumentReread, instrumentRead);

                            log.info("{} ok.", zipFileEntry.getName());
                            return null;
                        } catch (Exception e) {
                            log.warn("{} failed.", zipFileEntry.getName(), e);
                            return e;
                        }
                    })
                    .filter(Objects::nonNull)
                    .findAny().ifPresent((e) -> Assertions.fail());
        }
    }

    @Test
    void newSimple() throws IOException {
        String path = "/NewSimple.sin";
        testRoundtrip(path);
    }

    @Test
    void newSimpleLevel95() throws IOException {
        String path = "/NewSimpleLevel95.sin";
        testRoundtrip(path);
    }

    @Test
    void newSimpleDecay50() throws IOException {
        String path = "/NewSimpleDecay50.sin";
        testRoundtrip(path);
    }

    @Test
    void newSimpleCutOff80() throws IOException {
        String path = "/NewSimpleCutOff80.sin";
        testRoundtrip(path);
    }

    @Test
    void newSimpleHipassFilter() throws IOException {
        String path = "/NewSimpleHipass.sin";
        testRoundtrip(path);
    }

    @Test
    void newSimpleLoopOn() throws IOException {
        String path = "/NewSimpleLoopOn.sin";
        testRoundtrip(path);
    }


    @Test
    void newSimpleSemi8() throws IOException {
        String path = "/NewSimpleSemi8.sin";
        testRoundtrip(path);
    }


    @Test
    void newSimpleSemiMinus8() throws IOException {
        String path = "/NewSimpleSemi-8.sin";
        testRoundtrip(path);
    }


    @Test
    void newSimpleFineMinus10() throws IOException {
        String path = "/NewSimpleFine-10.sin";
        testRoundtrip(path);
    }

    @Test
    void newSimpleFine35() throws IOException {
        String path = "/NewSimpleFine35.sin";
        testRoundtrip(path);
    }


    @Test
    void newSimpleVelDecay70() throws IOException {
        String path = "/NewSimpleVelDecay70.sin";
        testRoundtrip(path);
    }

    @Test
    void newSimpleVelFilter75() throws IOException {
        String path = "/NewSimpleVelFilter75.sin";
        testRoundtrip(path);
    }

    @Test
    void newSimpleVelLevel82() throws IOException {
        String path = "/NewSimpleVelLevel82.sin";
        testRoundtrip(path);
    }

    @Test
    void newSimpleVelPitch94() throws IOException {
        String path = "/NewSimpleVelPitch94.sin";
        testRoundtrip(path);
    }


    @Test
    void newSimplePanMinus27() throws IOException {
        String path = "/NewSimplePan-27.sin";
        testRoundtrip(path);
    }

    @Test
    void newSimpleCycleModeRandom() throws IOException {
        String path = "/NewSimpleCycleRandom.sin";
        testRoundtrip(path);
    }

    @Test
    void hhCymbal5x1() throws IOException {
        String path = "/NewHHCymbal5x1.sin";
        testRoundtrip(path);
    }

    @Test
    void hhCymbal3x1() throws IOException {
        String path = "/NewHHCymbal3x1.sin";
        testRoundtrip(path);
    }

    @Test
    void hhPedal2x1() throws IOException {
        String path = "/NewHHPedal2x1.sin";
        testRoundtrip(path);
    }

    @Test
    void hhCymbal3x2() throws IOException {
        String path = "/NewHHCymbal3x2.sin";
        testRoundtrip(path);
    }

    private void testRoundtrip(String path) throws IOException {
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument orig = reader.read();

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        InstrumentWriter writer = new InstrumentWriter(bytesOut);
        writer.write(orig);
        writer.close();

        reader = new InstrumentReader(path, new ByteArrayInputStream(bytesOut.toByteArray()));
        Instrument read = reader.read();

        InstrumentAssertions.assertEqual(read, orig);
    }


}
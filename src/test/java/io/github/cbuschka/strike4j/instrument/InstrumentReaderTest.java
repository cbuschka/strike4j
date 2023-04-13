package io.github.cbuschka.strike4j.instrument;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Objects;
import java.util.zip.ZipFile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Slf4j
class InstrumentReaderTest {

    private final File backupZipFilePath = new File("strike-sdcard-bak.zip");

    @Test
    void canReadAllFromDrumModule() throws IOException {
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
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument read = reader.read();

        assertThat(read.getCutOff()).isEqualTo(99);
        assertThat(read.getDecay()).isEqualTo(98);
        assertThat(read.getLevel()).isEqualTo(90);
        assertThat(read.getFilterType()).isEqualTo(FilterType.LOPASS);
        assertThat(read.getSemi()).isEqualTo(0);
        assertThat(read.getFine()).isEqualTo(0);
        assertThat(read.getVelDecay()).isEqualTo(0);
    }

    @Test
    void newSimpleLevel95() throws IOException {
        String path = "/NewSimpleLevel95.sin";
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument read = reader.read();

        assertThat(read.getCutOff()).isEqualTo(99);
        assertThat(read.getDecay()).isEqualTo(98);
        assertThat(read.getLevel()).isEqualTo(95);
        assertThat(read.getFilterType()).isEqualTo(FilterType.LOPASS);
        assertThat(read.isLoopOn()).isFalse();
        assertThat(read.getSemi()).isEqualTo(0);
        assertThat(read.getFine()).isEqualTo(0);
        assertThat(read.getVelDecay()).isEqualTo(0);
    }

    @Test
    void newSimpleDecay50() throws IOException {
        String path = "/NewSimpleDecay50.sin";
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument read = reader.read();

        assertThat(read.getCutOff()).isEqualTo(99);
        assertThat(read.getDecay()).isEqualTo(50);
        assertThat(read.getLevel()).isEqualTo(90);
        assertThat(read.getFilterType()).isEqualTo(FilterType.LOPASS);
        assertThat(read.isLoopOn()).isFalse();
        assertThat(read.getSemi()).isEqualTo(0);
        assertThat(read.getFine()).isEqualTo(0);
        assertThat(read.getVelDecay()).isEqualTo(0);
    }

    @Test
    void newSimpleCutOff80() throws IOException {
        String path = "/NewSimpleCutOff80.sin";
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument read = reader.read();

        assertThat(read.getCutOff()).isEqualTo(80);
        assertThat(read.getDecay()).isEqualTo(98);
        assertThat(read.getLevel()).isEqualTo(90);
        assertThat(read.getFilterType()).isEqualTo(FilterType.LOPASS);
        assertThat(read.isLoopOn()).isFalse();
        assertThat(read.getSemi()).isEqualTo(0);
        assertThat(read.getFine()).isEqualTo(0);
        assertThat(read.getVelDecay()).isEqualTo(0);
    }

    @Test
    void newSimpleHipassFilter() throws IOException {
        String path = "/NewSimpleHipass.sin";
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument read = reader.read();

        assertThat(read.getCutOff()).isEqualTo(99);
        assertThat(read.getDecay()).isEqualTo(98);
        assertThat(read.getLevel()).isEqualTo(90);
        assertThat(read.getFilterType()).isEqualTo(FilterType.HIPASS);
        assertThat(read.isLoopOn()).isFalse();
        assertThat(read.getSemi()).isEqualTo(0);
        assertThat(read.getFine()).isEqualTo(0);
        assertThat(read.getVelDecay()).isEqualTo(0);
    }

    @Test
    void newSimpleLoopOn() throws IOException {
        String path = "/NewSimpleLoopOn.sin";
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument read = reader.read();

        assertThat(read.getCutOff()).isEqualTo(99);
        assertThat(read.getDecay()).isEqualTo(98);
        assertThat(read.getLevel()).isEqualTo(90);
        assertThat(read.getFilterType()).isEqualTo(FilterType.LOPASS);
        assertThat(read.isLoopOn()).isTrue();
        assertThat(read.getSemi()).isEqualTo(0);
        assertThat(read.getFine()).isEqualTo(0);
        assertThat(read.getVelDecay()).isEqualTo(0);
    }


    @Test
    void newSimpleSemi8() throws IOException {
        String path = "/NewSimpleSemi8.sin";
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument read = reader.read();

        assertThat(read.getCutOff()).isEqualTo(99);
        assertThat(read.getDecay()).isEqualTo(98);
        assertThat(read.getLevel()).isEqualTo(90);
        assertThat(read.getFilterType()).isEqualTo(FilterType.LOPASS);
        assertThat(read.isLoopOn()).isFalse();
        assertThat(read.getSemi()).isEqualTo(8);
        assertThat(read.getFine()).isEqualTo(0);
        assertThat(read.getVelDecay()).isEqualTo(0);
    }


    @Test
    void newSimpleSemiMinus8() throws IOException {
        String path = "/NewSimpleSemi-8.sin";
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument read = reader.read();

        assertThat(read.getCutOff()).isEqualTo(99);
        assertThat(read.getDecay()).isEqualTo(98);
        assertThat(read.getLevel()).isEqualTo(90);
        assertThat(read.getFilterType()).isEqualTo(FilterType.LOPASS);
        assertThat(read.isLoopOn()).isFalse();
        assertThat(read.getSemi()).isEqualTo(-8);
        assertThat(read.getFine()).isEqualTo(0);
        assertThat(read.getVelDecay()).isEqualTo(0);
    }


    @Test
    void newSimpleFineMinus10() throws IOException {
        String path = "/NewSimpleFine-10.sin";
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument read = reader.read();

        assertThat(read.getCutOff()).isEqualTo(99);
        assertThat(read.getDecay()).isEqualTo(98);
        assertThat(read.getLevel()).isEqualTo(90);
        assertThat(read.getFilterType()).isEqualTo(FilterType.LOPASS);
        assertThat(read.isLoopOn()).isFalse();
        assertThat(read.getSemi()).isEqualTo(0);
        assertThat(read.getFine()).isEqualTo(-10);
        assertThat(read.getVelDecay()).isEqualTo(0);
    }

    @Test
    void newSimpleFine35() throws IOException {
        String path = "/NewSimpleFine35.sin";
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument read = reader.read();

        assertThat(read.getCutOff()).isEqualTo(99);
        assertThat(read.getDecay()).isEqualTo(98);
        assertThat(read.getLevel()).isEqualTo(90);
        assertThat(read.getFilterType()).isEqualTo(FilterType.LOPASS);
        assertThat(read.isLoopOn()).isFalse();
        assertThat(read.getSemi()).isEqualTo(0);
        assertThat(read.getFine()).isEqualTo(35);
        assertThat(read.getVelDecay()).isEqualTo(0);
    }


    @Test
    void newSimpleVelDecay70() throws IOException {
        String path = "/NewSimpleVelDecay70.sin";
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument read = reader.read();

        assertThat(read.getCutOff()).isEqualTo(99);
        assertThat(read.getDecay()).isEqualTo(98);
        assertThat(read.getLevel()).isEqualTo(90);
        assertThat(read.getFilterType()).isEqualTo(FilterType.LOPASS);
        assertThat(read.isLoopOn()).isFalse();
        assertThat(read.getSemi()).isEqualTo(0);
        assertThat(read.getFine()).isEqualTo(0);
        assertThat(read.getVelDecay()).isEqualTo(70);
    }

    @Test
    void newSimpleVelFilter75() throws IOException {
        String path = "/NewSimpleVelFilter75.sin";
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument read = reader.read();

        assertThat(read.getCutOff()).isEqualTo(99);
        assertThat(read.getDecay()).isEqualTo(98);
        assertThat(read.getLevel()).isEqualTo(90);
        assertThat(read.getFilterType()).isEqualTo(FilterType.LOPASS);
        assertThat(read.isLoopOn()).isFalse();
        assertThat(read.getSemi()).isEqualTo(0);
        assertThat(read.getFine()).isEqualTo(0);
        assertThat(read.getVelDecay()).isEqualTo(0);
        assertThat(read.getVelFilter()).isEqualTo(75);
    }

    @Test
    void newSimpleVelLevel82() throws IOException {
        String path = "/NewSimpleVelLevel82.sin";
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument read = reader.read();

        assertThat(read.getCutOff()).isEqualTo(99);
        assertThat(read.getDecay()).isEqualTo(98);
        assertThat(read.getLevel()).isEqualTo(90);
        assertThat(read.getFilterType()).isEqualTo(FilterType.LOPASS);
        assertThat(read.isLoopOn()).isFalse();
        assertThat(read.getSemi()).isEqualTo(0);
        assertThat(read.getFine()).isEqualTo(0);
        assertThat(read.getVelDecay()).isEqualTo(0);
        assertThat(read.getVelFilter()).isEqualTo(0);
        assertThat(read.getVelLevel()).isEqualTo(82);
    }

    @Test
    void newSimpleVelPitch94() throws IOException {
        String path = "/NewSimpleVelPitch94.sin";
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument read = reader.read();

        assertThat(read.getCutOff()).isEqualTo(99);
        assertThat(read.getDecay()).isEqualTo(98);
        assertThat(read.getLevel()).isEqualTo(90);
        assertThat(read.getFilterType()).isEqualTo(FilterType.LOPASS);
        assertThat(read.isLoopOn()).isFalse();
        assertThat(read.getSemi()).isEqualTo(0);
        assertThat(read.getFine()).isEqualTo(0);
        assertThat(read.getVelDecay()).isEqualTo(0);
        assertThat(read.getVelFilter()).isEqualTo(0);
        assertThat(read.getVelLevel()).isEqualTo(90);
        assertThat(read.getVelPitch()).isEqualTo(94);
    }


    @Test
    void newSimplePanMinus27() throws IOException {
        String path = "/NewSimplePan-27.sin";
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument read = reader.read();

        assertThat(read.getCutOff()).isEqualTo(99);
        assertThat(read.getDecay()).isEqualTo(98);
        assertThat(read.getLevel()).isEqualTo(90);
        assertThat(read.getFilterType()).isEqualTo(FilterType.LOPASS);
        assertThat(read.isLoopOn()).isFalse();
        assertThat(read.getSemi()).isEqualTo(0);
        assertThat(read.getFine()).isEqualTo(0);
        assertThat(read.getVelDecay()).isEqualTo(0);
        assertThat(read.getVelFilter()).isEqualTo(0);
        assertThat(read.getVelLevel()).isEqualTo(90);
        assertThat(read.getVelPitch()).isEqualTo(0);
        assertThat(read.getPan()).isEqualTo(-27);
    }

    @Test
    void newSimpleCycleModeRandom() throws IOException {
        String path = "/NewSimpleCycleRandom.sin";
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument read = reader.read();

        assertThat(read.getCutOff()).isEqualTo(99);
        assertThat(read.getDecay()).isEqualTo(98);
        assertThat(read.getLevel()).isEqualTo(90);
        assertThat(read.getFilterType()).isEqualTo(FilterType.LOPASS);
        assertThat(read.isLoopOn()).isFalse();
        assertThat(read.getSemi()).isEqualTo(0);
        assertThat(read.getFine()).isEqualTo(0);
        assertThat(read.getVelDecay()).isEqualTo(0);
        assertThat(read.getVelFilter()).isEqualTo(0);
        assertThat(read.getVelLevel()).isEqualTo(90);
        assertThat(read.getVelPitch()).isEqualTo(0);
        assertThat(read.getPan()).isEqualTo(0);
        assertThat(read.getCycleMode()).isEqualTo(CycleMode.RANDOM);
    }

    @Test
    void hhCymbal5x1() throws IOException {
        String path = "/NewHHCymbal5x1.sin";
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument read = reader.read();
    }

    @Test
    void hhCymbal3x1() throws IOException {
        String path = "/NewHHCymbal3x1.sin";
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument read = reader.read();
    }

    @Test
    void hhPedal2x1() throws IOException {
        String path = "/NewHHPedal2x1.sin";
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument read = reader.read();
    }

    @Test
    void hhCymbal3x2() throws IOException {
        String path = "/NewHHCymbal3x2.sin";
        InputStream in = getClass().getResourceAsStream(path);
        InstrumentReader reader = new InstrumentReader(path, in);
        Instrument read = reader.read();
    }
}
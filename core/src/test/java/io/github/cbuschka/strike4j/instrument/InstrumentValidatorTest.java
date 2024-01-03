package io.github.cbuschka.strike4j.instrument;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Slf4j
class InstrumentValidatorTest {

    private final File backupZipFilePath = new File("strike-sdcard-bak.zip");

    private final InstrumentValidator validator = new InstrumentValidator();

    private Set<ConstraintViolation<Instrument>> violations;
    private Instrument instrument;

    @Test
    void canReadAndValidateAllFromDrumModule() throws IOException {
        assumeTrue(backupZipFilePath.isFile(), "SDCard backup required.");

        try (ZipFile zipFile = new ZipFile(backupZipFilePath);) {
            zipFile.stream()
                    .filter((zipFileEntry) -> zipFileEntry.getName().endsWith(".sin"))
                    .map((zipFileEntry) -> {
                        try {
                            InputStream in = zipFile.getInputStream(zipFileEntry);
                            InstrumentReader rd = new InstrumentReader(zipFileEntry.getName(),
                                    in);
                            Instrument instrument = rd.read(false);
                            Set<ConstraintViolation<Instrument>> violations = validator.validate(instrument);
                            if (!violations.isEmpty()) {
                                log.warn("{} has violations: {}", zipFileEntry.getName(), violations);
                            } else {
                                log.info("{} ok.", zipFileEntry.getName());
                            }
                            return violations;
                        } catch (Exception e) {
                            log.warn("{} failed.", zipFileEntry.getName(), e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .filter((v) -> !v.isEmpty())
                    .findAny().ifPresent((e) -> Assertions.fail());
        }
    }

    @Test
    void newSingle() {
        givenIsAnEmptyInstrument();

        whenInstrumentValidated();

        assertThat(violations.size()).isEqualTo(7);
        thenDetectsThatGroupIsMissing();
        thenDetectsThatSampleMappingsMissing();
        thenDetectsThatFilterTypeIsMissing();
        thenDetectsThatLevelIsMissing();
        thenDetectsThatCycleModeIsMissing();
        thenDetectsThatPathIsMissing();
        thenDetectsThatDecayIsMissing();
    }

    private void thenDetectsThatGroupIsMissing() {
        assertThat(violations
                .stream().anyMatch((c) -> c.getPropertyPath().toString().equals("group")
                        && c.getMessage().equals("must not be null"))).isTrue();
    }

    private void givenIsAnEmptyInstrument() {
        instrument = new Instrument();
    }

    private void whenInstrumentValidated() {
        violations = validator.validate(instrument);
    }

    private void thenDetectsThatDecayIsMissing() {
        assertThat(violations
                .stream().anyMatch((c) -> c.getPropertyPath().toString().equals("decay")
                        && c.getMessage().equals("must be greater than or equal to 1"))).isTrue();
    }

    private void thenDetectsThatPathIsMissing() {
        assertThat(violations
                .stream().anyMatch((c) -> c.getPropertyPath().toString().equals("path")
                        && c.getMessage().equals("must not be null"))).isTrue();
    }

    private void thenDetectsThatCycleModeIsMissing() {
        assertThat(violations
                .stream().anyMatch((c) -> c.getPropertyPath().toString().equals("cycleMode")
                        && c.getMessage().equals("must not be null"))).isTrue();
    }

    private void thenDetectsThatLevelIsMissing() {
        assertThat(violations
                .stream().anyMatch((c) -> c.getPropertyPath().toString().equals("level")
                        && c.getMessage().equals("must be greater than or equal to 1"))).isTrue();
    }

    private void thenDetectsThatFilterTypeIsMissing() {
        assertThat(violations
                .stream().anyMatch((c) -> c.getPropertyPath().toString().equals("filterType")
                        && c.getMessage().equals("must not be null"))).isTrue();
    }

    private void thenDetectsThatSampleMappingsMissing() {
        assertThat(violations
                .stream().anyMatch((c) -> c.getPropertyPath().toString().equals("sampleMappings")
                        && c.getMessage().equals("must not be empty"))).isTrue();
    }
}
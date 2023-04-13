package io.github.cbuschka.strike4j.instrument;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InstrumentGroupTest {

    @Test
    void verifyKickHasOrdinal0() {
        assertThat(InstrumentGroup.KICK.ordinal()).isEqualTo(0);
    }
}
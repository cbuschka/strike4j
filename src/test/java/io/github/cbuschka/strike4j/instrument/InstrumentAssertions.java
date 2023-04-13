package io.github.cbuschka.strike4j.instrument;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class InstrumentAssertions {

    public static void assertEqual(Instrument instrument, Instrument expected) {

        assertThat(instrument.getGroup()).isEqualTo(expected.getGroup());
        assertThat(instrument.getCutOff()).isEqualTo(expected.getCutOff());
        assertThat(instrument.getDecay()).isEqualTo(expected.getDecay());
        assertThat(instrument.getLevel()).isEqualTo(expected.getLevel());
        assertThat(instrument.getFilterType()).isEqualTo(expected.getFilterType());
        assertThat(instrument.isLoopOn()).isEqualTo(expected.isLoopOn());
        assertThat(instrument.getSemi()).isEqualTo(expected.getSemi());
        assertThat(instrument.getFine()).isEqualTo(expected.getFine());

        assertThat(instrument.getVelDecay()).isEqualTo(expected.getVelDecay());
        assertThat(instrument.getVelPitch()).isEqualTo(expected.getVelPitch());
        assertThat(instrument.getVelLevel()).isEqualTo(expected.getVelLevel());
        assertThat(instrument.getVelFilter()).isEqualTo(expected.getVelFilter());

        assertThat(instrument.getCycleMode()).isEqualTo(expected.getCycleMode());


        assertThat(instrument.getSampleMappings().size()).isEqualTo(expected.getSampleMappings().size());
        for (int i = 0; i < instrument.getSampleMappings().size(); ++i) {
            SampleMapping mapping = instrument.getSampleMappings().get(i);
            SampleMapping expectedMapping = expected.getSampleMappings().get(i);

            assertThat(mapping.getMinVelocity()).isEqualTo(expectedMapping.getMinVelocity());
            assertThat(mapping.getMaxVelocity()).isEqualTo(expectedMapping.getMaxVelocity());
            assertThat(mapping.getHihatOpenMin()).isEqualTo(expectedMapping.getHihatOpenMin());
            assertThat(mapping.getHihatOpenMax()).isEqualTo(expectedMapping.getHihatOpenMax());
            assertThat(mapping.getSamplePath()).isEqualTo(expectedMapping.getSamplePath());
        }
    }
}

package io.github.cbuschka.strike4j.instrument;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Instrument {
    @NotNull
    @Size(min = 1, max = Short.MAX_VALUE)
    private String path;

    @NotNull
    private InstrumentGroup group;

    @Min(1)
    @Max(99)
    private int level;

    @Min(-50)
    @Max(+50)
    private int pan;
    @Min(1)
    @Max(99)
    private int decay;
    @Min(0)
    @Max(127) // 99 from ui
    private int cutOff;
    @NotNull
    private FilterType filterType;
    private boolean loopOn;
    @Min(-12)
    @Max(+12)
    private int semi;
    @Min(-50)
    @Max(+50)
    private int fine;
    @Min(-99)
    @Max(+99)
    private int velDecay;
    @Min(-99)
    @Max(+99)
    private int velFilter;
    @Min(0)
    @Max(+99)
    private int velLevel;
    @Min(-99)
    @Max(+99)
    private int velPitch;
    @NotNull
    private CycleMode cycleMode;
    @NotNull
    @NotEmpty
    private List<@NotNull SampleMapping> sampleMappings = new ArrayList<>();
}
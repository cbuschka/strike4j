package io.github.cbuschka.strike4j.instrument;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Instrument {
    private String path;
    private int level;
    private int pan;
    private int decay;
    private int cutOff;
    private FilterType filterType;
    private boolean loopOn;
    private int semi;
    private int fine;
    private int velDecay;
    private int velFilter;
    private int velLevel;
    private int velPitch;
    private CycleMode cycleMode;
    private List<SampleMapping> sampleMappings = new ArrayList<>();
}

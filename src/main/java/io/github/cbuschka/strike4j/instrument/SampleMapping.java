package io.github.cbuschka.strike4j.instrument;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SampleMapping {
    private int minVelocity;
    private int maxVelocity;
    private int hihatOpenMin;
    private int hihatOpenMax;
    private String samplePath;
}

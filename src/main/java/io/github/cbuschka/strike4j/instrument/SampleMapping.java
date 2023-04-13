package io.github.cbuschka.strike4j.instrument;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SampleMapping {
    @Min(1)
    @Max(127)
    private int minVelocity;
    @Min(1)
    @Max(127)
    private int maxVelocity;
    @Min(0)
    @Max(127)
    private int hihatOpenMin;
    @Min(0)
    @Max(127)
    private int hihatOpenMax;
    @NonNull
    @NotEmpty
    private String samplePath;
}

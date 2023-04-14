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

    private int command = 0x63;
    private int unknown2 = 0;
    private int unknown3 = 0x7f;
    private int unknown4 = 0;
    private int unknown5 = 0;
    private int unknown6 = 0;
    private int unknown7 = 0;
    private int unknown8 = 0;
    private int unknown9 = 0;
    private int unknown10 = 0;
    private int unknown11 = 0;
    private int unknown12 = 0;
    private int unknown13 = 1;
}

package io.github.cbuschka.strike4j.instrument;

import java.util.NoSuchElementException;

public enum InstrumentGroup {
    KICK,
    SNARE,
    TOM,
    HH,
    CRASH,
    RIDE,
    UNKNOWN6,
    E_KICK,
    E_SNARE,
    E_TOM,
    CHINA_SPLASHES,
    PERC_ETHNIC,
    UNKNOWN12,
    PERC_ORCHESTRAL,
    PERCUSSION,
    UNKNOWN15,
    UNKNOWN16,
    UNKNOWN17,
    CLAPS_SFX,
    MELODIC;

    public static InstrumentGroup valueOf(int x) {
        for (InstrumentGroup value : values()) {
            if (value.ordinal() == x) {
                return value;
            }
        }

        throw new NoSuchElementException("No enum for ordinal " + x + ".");
    }
}
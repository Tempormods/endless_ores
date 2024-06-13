package net.minecraft.util;

import java.util.concurrent.TimeUnit;
import net.minecraft.util.valueproviders.UniformInt;

public class TimeUtil {
    public static final long NANOSECONDS_PER_SECOND = TimeUnit.SECONDS.toNanos(1L);
    public static final long NANOSECONDS_PER_MILLISECOND = TimeUnit.MILLISECONDS.toNanos(1L);
    public static final long f_302812_ = TimeUnit.SECONDS.toMillis(1L);
    public static final long f_303069_ = TimeUnit.HOURS.toSeconds(1L);
    public static final int f_315347_ = (int)TimeUnit.MINUTES.toSeconds(1L);

    public static UniformInt rangeOfSeconds(int pMinInclusive, int pMaxInclusive) {
        return UniformInt.of(pMinInclusive * 20, pMaxInclusive * 20);
    }
}
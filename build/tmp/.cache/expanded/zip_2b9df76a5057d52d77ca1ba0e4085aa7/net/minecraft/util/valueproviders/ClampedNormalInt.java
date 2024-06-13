package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class ClampedNormalInt extends IntProvider {
    public static final MapCodec<ClampedNormalInt> CODEC = RecordCodecBuilder.<ClampedNormalInt>mapCodec(
            p_185887_ -> p_185887_.group(
                        Codec.FLOAT.fieldOf("mean").forGetter(p_185905_ -> p_185905_.mean),
                        Codec.FLOAT.fieldOf("deviation").forGetter(p_185903_ -> p_185903_.deviation),
                        Codec.INT.fieldOf("min_inclusive").forGetter(p_326736_ -> p_326736_.f_315784_),
                        Codec.INT.fieldOf("max_inclusive").forGetter(p_326737_ -> p_326737_.f_316795_)
                    )
                    .apply(p_185887_, ClampedNormalInt::new)
        )
        .validate(
            p_326735_ -> p_326735_.f_316795_ < p_326735_.f_315784_
                    ? DataResult.error(() -> "Max must be larger than min: [" + p_326735_.f_315784_ + ", " + p_326735_.f_316795_ + "]")
                    : DataResult.success(p_326735_)
        );
    private final float mean;
    private final float deviation;
    private final int f_315784_;
    private final int f_316795_;

    public static ClampedNormalInt of(float pMean, float pDeviation, int pMinInclusive, int pMaxInclusive) {
        return new ClampedNormalInt(pMean, pDeviation, pMinInclusive, pMaxInclusive);
    }

    private ClampedNormalInt(float p_185874_, float p_185875_, int p_185876_, int p_185877_) {
        this.mean = p_185874_;
        this.deviation = p_185875_;
        this.f_315784_ = p_185876_;
        this.f_316795_ = p_185877_;
    }

    @Override
    public int sample(RandomSource pRandom) {
        return sample(pRandom, this.mean, this.deviation, (float)this.f_315784_, (float)this.f_316795_);
    }

    public static int sample(RandomSource pRandom, float pMean, float pDeviation, float pMinInclusive, float pMaxInclusive) {
        return (int)Mth.clamp(Mth.normal(pRandom, pMean, pDeviation), pMinInclusive, pMaxInclusive);
    }

    @Override
    public int getMinValue() {
        return this.f_315784_;
    }

    @Override
    public int getMaxValue() {
        return this.f_316795_;
    }

    @Override
    public IntProviderType<?> getType() {
        return IntProviderType.CLAMPED_NORMAL;
    }

    @Override
    public String toString() {
        return "normal(" + this.mean + ", " + this.deviation + ") in [" + this.f_315784_ + "-" + this.f_316795_ + "]";
    }
}
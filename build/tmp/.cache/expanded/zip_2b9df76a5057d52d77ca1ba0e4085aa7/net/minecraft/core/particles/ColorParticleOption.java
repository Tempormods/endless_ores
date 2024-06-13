package net.minecraft.core.particles;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.FastColor;

public class ColorParticleOption implements ParticleOptions {
    private final ParticleType<ColorParticleOption> f_316705_;
    private final int f_316123_;

    public static MapCodec<ColorParticleOption> m_321856_(ParticleType<ColorParticleOption> p_329922_) {
        return ExtraCodecs.f_315502_.xmap(p_335886_ -> new ColorParticleOption(p_329922_, p_335886_), p_328917_ -> p_328917_.f_316123_).fieldOf("color");
    }

    public static StreamCodec<? super ByteBuf, ColorParticleOption> m_323084_(ParticleType<ColorParticleOption> p_328683_) {
        return ByteBufCodecs.f_316612_.m_323038_(p_330079_ -> new ColorParticleOption(p_328683_, p_330079_), p_329364_ -> p_329364_.f_316123_);
    }

    private ColorParticleOption(ParticleType<ColorParticleOption> p_330442_, int p_329966_) {
        this.f_316705_ = p_330442_;
        this.f_316123_ = p_329966_;
    }

    @Override
    public ParticleType<ColorParticleOption> getType() {
        return this.f_316705_;
    }

    public float m_320442_() {
        return (float)FastColor.ARGB32.red(this.f_316123_) / 255.0F;
    }

    public float m_319328_() {
        return (float)FastColor.ARGB32.green(this.f_316123_) / 255.0F;
    }

    public float m_321365_() {
        return (float)FastColor.ARGB32.blue(this.f_316123_) / 255.0F;
    }

    public float m_323169_() {
        return (float)FastColor.ARGB32.alpha(this.f_316123_) / 255.0F;
    }

    public static ColorParticleOption m_318840_(ParticleType<ColorParticleOption> p_329254_, int p_327671_) {
        return new ColorParticleOption(p_329254_, p_327671_);
    }

    public static ColorParticleOption m_321894_(ParticleType<ColorParticleOption> p_328973_, float p_334118_, float p_330068_, float p_330217_) {
        return m_318840_(p_328973_, FastColor.ARGB32.m_323842_(1.0F, p_334118_, p_330068_, p_330217_));
    }
}
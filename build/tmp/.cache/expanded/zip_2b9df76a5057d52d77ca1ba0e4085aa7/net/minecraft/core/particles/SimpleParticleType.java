package net.minecraft.core.particles;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class SimpleParticleType extends ParticleType<SimpleParticleType> implements ParticleOptions {
    private final MapCodec<SimpleParticleType> codec = MapCodec.unit(this::getType);
    private final StreamCodec<RegistryFriendlyByteBuf, SimpleParticleType> f_316940_ = StreamCodec.m_323136_(this);

    public SimpleParticleType(boolean pOverrideLimiter) {
        super(pOverrideLimiter);
    }

    public SimpleParticleType getType() {
        return this;
    }

    @Override
    public MapCodec<SimpleParticleType> codec() {
        return this.codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SimpleParticleType> m_319843_() {
        return this.f_316940_;
    }
}
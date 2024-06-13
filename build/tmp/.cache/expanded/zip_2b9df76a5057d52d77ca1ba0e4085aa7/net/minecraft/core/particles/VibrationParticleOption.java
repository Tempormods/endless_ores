package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;

public class VibrationParticleOption implements ParticleOptions {
    private static final Codec<PositionSource> f_315170_ = PositionSource.CODEC
        .validate(
            p_325816_ -> p_325816_ instanceof EntityPositionSource
                    ? DataResult.error(() -> "Entity position sources are not allowed")
                    : DataResult.success(p_325816_)
        );
    public static final MapCodec<VibrationParticleOption> CODEC = RecordCodecBuilder.mapCodec(
        p_325815_ -> p_325815_.group(
                    f_315170_.fieldOf("destination").forGetter(VibrationParticleOption::getDestination),
                    Codec.INT.fieldOf("arrival_in_ticks").forGetter(VibrationParticleOption::getArrivalInTicks)
                )
                .apply(p_325815_, VibrationParticleOption::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, VibrationParticleOption> f_315797_ = StreamCodec.m_320349_(
        PositionSource.f_315179_, VibrationParticleOption::getDestination, ByteBufCodecs.f_316730_, VibrationParticleOption::getArrivalInTicks, VibrationParticleOption::new
    );
    private final PositionSource destination;
    private final int arrivalInTicks;

    public VibrationParticleOption(PositionSource p_235975_, int p_235976_) {
        this.destination = p_235975_;
        this.arrivalInTicks = p_235976_;
    }

    @Override
    public ParticleType<VibrationParticleOption> getType() {
        return ParticleTypes.VIBRATION;
    }

    public PositionSource getDestination() {
        return this.destination;
    }

    public int getArrivalInTicks() {
        return this.arrivalInTicks;
    }
}
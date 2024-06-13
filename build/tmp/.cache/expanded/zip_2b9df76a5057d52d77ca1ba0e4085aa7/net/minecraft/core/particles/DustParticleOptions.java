package net.minecraft.core.particles;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class DustParticleOptions extends ScalableParticleOptionsBase {
    public static final Vector3f REDSTONE_PARTICLE_COLOR = Vec3.fromRGB24(16711680).toVector3f();
    public static final DustParticleOptions REDSTONE = new DustParticleOptions(REDSTONE_PARTICLE_COLOR, 1.0F);
    public static final MapCodec<DustParticleOptions> CODEC = RecordCodecBuilder.mapCodec(
        p_325799_ -> p_325799_.group(
                    ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(p_253371_ -> p_253371_.f_316445_),
                    f_313903_.fieldOf("scale").forGetter(ScalableParticleOptionsBase::m_321296_)
                )
                .apply(p_325799_, DustParticleOptions::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, DustParticleOptions> f_315304_ = StreamCodec.m_320349_(
        ByteBufCodecs.f_314483_, p_325798_ -> p_325798_.f_316445_, ByteBufCodecs.f_314734_, ScalableParticleOptionsBase::m_321296_, DustParticleOptions::new
    );
    private final Vector3f f_316445_;

    public DustParticleOptions(Vector3f p_253868_, float p_254154_) {
        super(p_254154_);
        this.f_316445_ = p_253868_;
    }

    @Override
    public ParticleType<DustParticleOptions> getType() {
        return ParticleTypes.DUST;
    }

    public Vector3f m_323596_() {
        return this.f_316445_;
    }
}
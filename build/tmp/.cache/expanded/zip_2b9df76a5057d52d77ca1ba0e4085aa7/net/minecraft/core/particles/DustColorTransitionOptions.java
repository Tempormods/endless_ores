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

public class DustColorTransitionOptions extends ScalableParticleOptionsBase {
    public static final Vector3f SCULK_PARTICLE_COLOR = Vec3.fromRGB24(3790560).toVector3f();
    public static final DustColorTransitionOptions SCULK_TO_REDSTONE = new DustColorTransitionOptions(SCULK_PARTICLE_COLOR, DustParticleOptions.REDSTONE_PARTICLE_COLOR, 1.0F);
    public static final MapCodec<DustColorTransitionOptions> CODEC = RecordCodecBuilder.mapCodec(
        p_325794_ -> p_325794_.group(
                    ExtraCodecs.VECTOR3F.fieldOf("from_color").forGetter(p_325797_ -> p_325797_.f_316521_),
                    ExtraCodecs.VECTOR3F.fieldOf("to_color").forGetter(p_253367_ -> p_253367_.toColor),
                    f_313903_.fieldOf("scale").forGetter(ScalableParticleOptionsBase::m_321296_)
                )
                .apply(p_325794_, DustColorTransitionOptions::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, DustColorTransitionOptions> f_316000_ = StreamCodec.m_321516_(
        ByteBufCodecs.f_314483_,
        p_325795_ -> p_325795_.f_316521_,
        ByteBufCodecs.f_314483_,
        p_325796_ -> p_325796_.toColor,
        ByteBufCodecs.f_314734_,
        ScalableParticleOptionsBase::m_321296_,
        DustColorTransitionOptions::new
    );
    private final Vector3f f_316521_;
    private final Vector3f toColor;

    public DustColorTransitionOptions(Vector3f p_254199_, Vector3f p_254529_, float p_254178_) {
        super(p_254178_);
        this.f_316521_ = p_254199_;
        this.toColor = p_254529_;
    }

    public Vector3f getFromColor() {
        return this.f_316521_;
    }

    public Vector3f getToColor() {
        return this.toColor;
    }

    @Override
    public ParticleType<DustColorTransitionOptions> getType() {
        return ParticleTypes.DUST_COLOR_TRANSITION;
    }
}
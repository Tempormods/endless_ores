package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public record SuspiciousStewEffects(List<SuspiciousStewEffects.Entry> f_315993_) {
    public static final SuspiciousStewEffects f_314102_ = new SuspiciousStewEffects(List.of());
    public static final Codec<SuspiciousStewEffects> f_314598_ = SuspiciousStewEffects.Entry.f_316274_
        .listOf()
        .xmap(SuspiciousStewEffects::new, SuspiciousStewEffects::f_315993_);
    public static final StreamCodec<RegistryFriendlyByteBuf, SuspiciousStewEffects> f_316377_ = SuspiciousStewEffects.Entry.f_316892_
        .m_321801_(ByteBufCodecs.m_324765_())
        .m_323038_(SuspiciousStewEffects::new, SuspiciousStewEffects::f_315993_);

    public SuspiciousStewEffects m_323377_(SuspiciousStewEffects.Entry p_330002_) {
        return new SuspiciousStewEffects(Util.m_324319_(this.f_315993_, p_330002_));
    }

    public static record Entry(Holder<MobEffect> f_315280_, int f_316640_) {
        public static final Codec<SuspiciousStewEffects.Entry> f_316274_ = RecordCodecBuilder.create(
            p_327728_ -> p_327728_.group(
                        BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("id").forGetter(SuspiciousStewEffects.Entry::f_315280_),
                        Codec.INT.lenientOptionalFieldOf("duration", Integer.valueOf(160)).forGetter(SuspiciousStewEffects.Entry::f_316640_)
                    )
                    .apply(p_327728_, SuspiciousStewEffects.Entry::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, SuspiciousStewEffects.Entry> f_316892_ = StreamCodec.m_320349_(
            ByteBufCodecs.m_322636_(Registries.MOB_EFFECT),
            SuspiciousStewEffects.Entry::f_315280_,
            ByteBufCodecs.f_316730_,
            SuspiciousStewEffects.Entry::f_316640_,
            SuspiciousStewEffects.Entry::new
        );

        public MobEffectInstance m_320712_() {
            return new MobEffectInstance(this.f_315280_, this.f_316640_);
        }
    }
}
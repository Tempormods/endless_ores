package net.minecraft.world.food;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffectInstance;

public record FoodProperties(int nutrition, float f_315723_, boolean canAlwaysEat, float f_314902_, List<FoodProperties.PossibleEffect> effects) {
    private static final float f_314525_ = 1.6F;
    public static final Codec<FoodProperties> f_316600_ = RecordCodecBuilder.create(
        p_336121_ -> p_336121_.group(
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("nutrition").forGetter(FoodProperties::nutrition),
                    Codec.FLOAT.fieldOf("saturation").forGetter(FoodProperties::f_315723_),
                    Codec.BOOL.optionalFieldOf("can_always_eat", Boolean.valueOf(false)).forGetter(FoodProperties::canAlwaysEat),
                    ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("eat_seconds", 1.6F).forGetter(FoodProperties::f_314902_),
                    FoodProperties.PossibleEffect.f_317026_.listOf().optionalFieldOf("effects", List.of()).forGetter(FoodProperties::effects)
                )
                .apply(p_336121_, FoodProperties::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, FoodProperties> f_317144_ = StreamCodec.m_319894_(
        ByteBufCodecs.f_316730_,
        FoodProperties::nutrition,
        ByteBufCodecs.f_314734_,
        FoodProperties::f_315723_,
        ByteBufCodecs.f_315514_,
        FoodProperties::canAlwaysEat,
        ByteBufCodecs.f_314734_,
        FoodProperties::f_314902_,
        FoodProperties.PossibleEffect.f_317005_.m_321801_(ByteBufCodecs.m_324765_()),
        FoodProperties::effects,
        FoodProperties::new
    );

    public int m_319390_() {
        return (int)(this.f_314902_ * 20.0F);
    }

    public static class Builder {
        private int nutrition;
        private float saturationModifier;
        private boolean canAlwaysEat;
        private float f_316145_ = 1.6F;
        private final ImmutableList.Builder<FoodProperties.PossibleEffect> effects = ImmutableList.builder();

        public FoodProperties.Builder nutrition(int pNutrition) {
            this.nutrition = pNutrition;
            return this;
        }

        public FoodProperties.Builder saturationMod(float pSaturationModifier) {
            this.saturationModifier = pSaturationModifier;
            return this;
        }

        public FoodProperties.Builder alwaysEat() {
            this.canAlwaysEat = true;
            return this;
        }

        public FoodProperties.Builder fast() {
            this.f_316145_ = 0.8F;
            return this;
        }

        public FoodProperties.Builder effect(MobEffectInstance pEffect, float pProbability) {
            this.effects.add(new FoodProperties.PossibleEffect(pEffect, pProbability));
            return this;
        }

        public FoodProperties build() {
            float f = FoodConstants.m_324902_(this.nutrition, this.saturationModifier);
            return new FoodProperties(this.nutrition, f, this.canAlwaysEat, this.f_316145_, this.effects.build());
        }
    }

    public static record PossibleEffect(MobEffectInstance f_314201_, float f_314917_) {
        public static final Codec<FoodProperties.PossibleEffect> f_317026_ = RecordCodecBuilder.create(
            p_333209_ -> p_333209_.group(
                        MobEffectInstance.f_316026_.fieldOf("effect").forGetter(FoodProperties.PossibleEffect::m_319029_),
                        Codec.floatRange(0.0F, 1.0F).optionalFieldOf("probability", 1.0F).forGetter(FoodProperties.PossibleEffect::f_314917_)
                    )
                    .apply(p_333209_, FoodProperties.PossibleEffect::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, FoodProperties.PossibleEffect> f_317005_ = StreamCodec.m_320349_(
            MobEffectInstance.f_315755_,
            FoodProperties.PossibleEffect::m_319029_,
            ByteBufCodecs.f_314734_,
            FoodProperties.PossibleEffect::f_314917_,
            FoodProperties.PossibleEffect::new
        );

        public MobEffectInstance m_319029_() {
            return new MobEffectInstance(this.f_314201_);
        }
    }
}
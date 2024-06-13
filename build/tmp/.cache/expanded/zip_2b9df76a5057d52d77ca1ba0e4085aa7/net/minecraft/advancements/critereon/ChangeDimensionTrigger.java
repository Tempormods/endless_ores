package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class ChangeDimensionTrigger extends SimpleCriterionTrigger<ChangeDimensionTrigger.TriggerInstance> {
    @Override
    public Codec<ChangeDimensionTrigger.TriggerInstance> createInstance() {
        return ChangeDimensionTrigger.TriggerInstance.f_302776_;
    }

    public void trigger(ServerPlayer pPlayer, ResourceKey<Level> pFromLevel, ResourceKey<Level> pToLevel) {
        this.trigger(pPlayer, p_19768_ -> p_19768_.matches(pFromLevel, pToLevel));
    }

    public static record TriggerInstance(
        Optional<ContextAwarePredicate> f_302207_, Optional<ResourceKey<Level>> from, Optional<ResourceKey<Level>> to
    ) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<ChangeDimensionTrigger.TriggerInstance> f_302776_ = RecordCodecBuilder.create(
            p_325194_ -> p_325194_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(ChangeDimensionTrigger.TriggerInstance::playerPredicate),
                        ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("from").forGetter(ChangeDimensionTrigger.TriggerInstance::from),
                        ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("to").forGetter(ChangeDimensionTrigger.TriggerInstance::to)
                    )
                    .apply(p_325194_, ChangeDimensionTrigger.TriggerInstance::new)
        );

        public static Criterion<ChangeDimensionTrigger.TriggerInstance> changedDimension() {
            return CriteriaTriggers.CHANGED_DIMENSION.createCriterion(new ChangeDimensionTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
        }

        public static Criterion<ChangeDimensionTrigger.TriggerInstance> changedDimension(ResourceKey<Level> pFrom, ResourceKey<Level> pTo) {
            return CriteriaTriggers.CHANGED_DIMENSION
                .createCriterion(new ChangeDimensionTrigger.TriggerInstance(Optional.empty(), Optional.of(pFrom), Optional.of(pTo)));
        }

        public static Criterion<ChangeDimensionTrigger.TriggerInstance> changedDimensionTo(ResourceKey<Level> pTo) {
            return CriteriaTriggers.CHANGED_DIMENSION.createCriterion(new ChangeDimensionTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(pTo)));
        }

        public static Criterion<ChangeDimensionTrigger.TriggerInstance> changedDimensionFrom(ResourceKey<Level> pFrom) {
            return CriteriaTriggers.CHANGED_DIMENSION.createCriterion(new ChangeDimensionTrigger.TriggerInstance(Optional.empty(), Optional.of(pFrom), Optional.empty()));
        }

        public boolean matches(ResourceKey<Level> pFromLevel, ResourceKey<Level> pToLevel) {
            return this.from.isPresent() && this.from.get() != pFromLevel ? false : !this.to.isPresent() || this.to.get() == pToLevel;
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_302207_;
        }
    }
}
package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class EffectsChangedTrigger extends SimpleCriterionTrigger<EffectsChangedTrigger.TriggerInstance> {
    @Override
    public Codec<EffectsChangedTrigger.TriggerInstance> createInstance() {
        return EffectsChangedTrigger.TriggerInstance.f_302946_;
    }

    public void trigger(ServerPlayer pPlayer, @Nullable Entity pSource) {
        LootContext lootcontext = pSource != null ? EntityPredicate.createContext(pPlayer, pSource) : null;
        this.trigger(pPlayer, p_149268_ -> p_149268_.matches(pPlayer, lootcontext));
    }

    public static record TriggerInstance(
        Optional<ContextAwarePredicate> f_303395_, Optional<MobEffectsPredicate> effects, Optional<ContextAwarePredicate> source
    ) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<EffectsChangedTrigger.TriggerInstance> f_302946_ = RecordCodecBuilder.create(
            p_325203_ -> p_325203_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(EffectsChangedTrigger.TriggerInstance::playerPredicate),
                        MobEffectsPredicate.CODEC.optionalFieldOf("effects").forGetter(EffectsChangedTrigger.TriggerInstance::effects),
                        EntityPredicate.f_303210_.optionalFieldOf("source").forGetter(EffectsChangedTrigger.TriggerInstance::source)
                    )
                    .apply(p_325203_, EffectsChangedTrigger.TriggerInstance::new)
        );

        public static Criterion<EffectsChangedTrigger.TriggerInstance> hasEffects(MobEffectsPredicate.Builder pEffects) {
            return CriteriaTriggers.EFFECTS_CHANGED.createCriterion(new EffectsChangedTrigger.TriggerInstance(Optional.empty(), pEffects.build(), Optional.empty()));
        }

        public static Criterion<EffectsChangedTrigger.TriggerInstance> gotEffectsFrom(EntityPredicate.Builder pSource) {
            return CriteriaTriggers.EFFECTS_CHANGED
                .createCriterion(
                    new EffectsChangedTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(EntityPredicate.wrap(pSource.build())))
                );
        }

        public boolean matches(ServerPlayer pPlayer, @Nullable LootContext pLootContext) {
            return this.effects.isPresent() && !this.effects.get().matches(pPlayer)
                ? false
                : !this.source.isPresent() || pLootContext != null && this.source.get().matches(pLootContext);
        }

        @Override
        public void serializeToJson(CriterionValidator p_312004_) {
            SimpleCriterionTrigger.SimpleInstance.super.serializeToJson(p_312004_);
            p_312004_.m_307484_(this.source, ".source");
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_303395_;
        }
    }
}
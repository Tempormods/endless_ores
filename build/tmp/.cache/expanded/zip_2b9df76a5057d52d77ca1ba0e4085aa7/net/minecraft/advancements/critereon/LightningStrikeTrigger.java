package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.storage.loot.LootContext;

public class LightningStrikeTrigger extends SimpleCriterionTrigger<LightningStrikeTrigger.TriggerInstance> {
    @Override
    public Codec<LightningStrikeTrigger.TriggerInstance> createInstance() {
        return LightningStrikeTrigger.TriggerInstance.f_303280_;
    }

    public void trigger(ServerPlayer pPlayer, LightningBolt pLightning, List<Entity> pNearbyEntities) {
        List<LootContext> list = pNearbyEntities.stream().map(p_153390_ -> EntityPredicate.createContext(pPlayer, p_153390_)).collect(Collectors.toList());
        LootContext lootcontext = EntityPredicate.createContext(pPlayer, pLightning);
        this.trigger(pPlayer, p_153402_ -> p_153402_.matches(lootcontext, list));
    }

    public static record TriggerInstance(
        Optional<ContextAwarePredicate> f_303589_, Optional<ContextAwarePredicate> lightning, Optional<ContextAwarePredicate> bystander
    ) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<LightningStrikeTrigger.TriggerInstance> f_303280_ = RecordCodecBuilder.create(
            p_325228_ -> p_325228_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(LightningStrikeTrigger.TriggerInstance::playerPredicate),
                        EntityPredicate.f_303210_.optionalFieldOf("lightning").forGetter(LightningStrikeTrigger.TriggerInstance::lightning),
                        EntityPredicate.f_303210_.optionalFieldOf("bystander").forGetter(LightningStrikeTrigger.TriggerInstance::bystander)
                    )
                    .apply(p_325228_, LightningStrikeTrigger.TriggerInstance::new)
        );

        public static Criterion<LightningStrikeTrigger.TriggerInstance> lightningStrike(Optional<EntityPredicate> pLightning, Optional<EntityPredicate> pBystander) {
            return CriteriaTriggers.LIGHTNING_STRIKE
                .createCriterion(
                    new LightningStrikeTrigger.TriggerInstance(Optional.empty(), EntityPredicate.wrap(pLightning), EntityPredicate.wrap(pBystander))
                );
        }

        public boolean matches(LootContext pPlayerContext, List<LootContext> pEntityContexts) {
            return this.lightning.isPresent() && !this.lightning.get().matches(pPlayerContext)
                ? false
                : !this.bystander.isPresent() || !pEntityContexts.stream().noneMatch(this.bystander.get()::matches);
        }

        @Override
        public void serializeToJson(CriterionValidator p_312134_) {
            SimpleCriterionTrigger.SimpleInstance.super.serializeToJson(p_312134_);
            p_312134_.m_307484_(this.lightning, ".lightning");
            p_312134_.m_307484_(this.bystander, ".bystander");
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_303589_;
        }
    }
}
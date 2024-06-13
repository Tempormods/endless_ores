package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class EntityHurtPlayerTrigger extends SimpleCriterionTrigger<EntityHurtPlayerTrigger.TriggerInstance> {
    @Override
    public Codec<EntityHurtPlayerTrigger.TriggerInstance> createInstance() {
        return EntityHurtPlayerTrigger.TriggerInstance.f_302475_;
    }

    public void trigger(ServerPlayer pPlayer, DamageSource pSource, float pDealtDamage, float pTakenDamage, boolean pBlocked) {
        this.trigger(pPlayer, p_35186_ -> p_35186_.matches(pPlayer, pSource, pDealtDamage, pTakenDamage, pBlocked));
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> f_303582_, Optional<DamagePredicate> damage)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<EntityHurtPlayerTrigger.TriggerInstance> f_302475_ = RecordCodecBuilder.create(
            p_325211_ -> p_325211_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(EntityHurtPlayerTrigger.TriggerInstance::playerPredicate),
                        DamagePredicate.f_303816_.optionalFieldOf("damage").forGetter(EntityHurtPlayerTrigger.TriggerInstance::damage)
                    )
                    .apply(p_325211_, EntityHurtPlayerTrigger.TriggerInstance::new)
        );

        public static Criterion<EntityHurtPlayerTrigger.TriggerInstance> entityHurtPlayer() {
            return CriteriaTriggers.ENTITY_HURT_PLAYER.createCriterion(new EntityHurtPlayerTrigger.TriggerInstance(Optional.empty(), Optional.empty()));
        }

        public static Criterion<EntityHurtPlayerTrigger.TriggerInstance> entityHurtPlayer(DamagePredicate pDamage) {
            return CriteriaTriggers.ENTITY_HURT_PLAYER.createCriterion(new EntityHurtPlayerTrigger.TriggerInstance(Optional.empty(), Optional.of(pDamage)));
        }

        public static Criterion<EntityHurtPlayerTrigger.TriggerInstance> entityHurtPlayer(DamagePredicate.Builder pDamage) {
            return CriteriaTriggers.ENTITY_HURT_PLAYER.createCriterion(new EntityHurtPlayerTrigger.TriggerInstance(Optional.empty(), Optional.of(pDamage.build())));
        }

        public boolean matches(ServerPlayer pPlayer, DamageSource pSource, float pDealtDamage, float pTakenDamage, boolean pBlocked) {
            return !this.damage.isPresent() || this.damage.get().matches(pPlayer, pSource, pDealtDamage, pTakenDamage, pBlocked);
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_303582_;
        }
    }
}
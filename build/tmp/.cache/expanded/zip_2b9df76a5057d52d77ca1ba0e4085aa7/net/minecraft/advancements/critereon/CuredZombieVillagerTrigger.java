package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.storage.loot.LootContext;

public class CuredZombieVillagerTrigger extends SimpleCriterionTrigger<CuredZombieVillagerTrigger.TriggerInstance> {
    @Override
    public Codec<CuredZombieVillagerTrigger.TriggerInstance> createInstance() {
        return CuredZombieVillagerTrigger.TriggerInstance.f_303115_;
    }

    public void trigger(ServerPlayer pPlayer, Zombie pZombie, Villager pVillager) {
        LootContext lootcontext = EntityPredicate.createContext(pPlayer, pZombie);
        LootContext lootcontext1 = EntityPredicate.createContext(pPlayer, pVillager);
        this.trigger(pPlayer, p_24285_ -> p_24285_.matches(lootcontext, lootcontext1));
    }

    public static record TriggerInstance(
        Optional<ContextAwarePredicate> f_302742_, Optional<ContextAwarePredicate> zombie, Optional<ContextAwarePredicate> villager
    ) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<CuredZombieVillagerTrigger.TriggerInstance> f_303115_ = RecordCodecBuilder.create(
            p_325198_ -> p_325198_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(CuredZombieVillagerTrigger.TriggerInstance::playerPredicate),
                        EntityPredicate.f_303210_.optionalFieldOf("zombie").forGetter(CuredZombieVillagerTrigger.TriggerInstance::zombie),
                        EntityPredicate.f_303210_.optionalFieldOf("villager").forGetter(CuredZombieVillagerTrigger.TriggerInstance::villager)
                    )
                    .apply(p_325198_, CuredZombieVillagerTrigger.TriggerInstance::new)
        );

        public static Criterion<CuredZombieVillagerTrigger.TriggerInstance> curedZombieVillager() {
            return CriteriaTriggers.CURED_ZOMBIE_VILLAGER.createCriterion(new CuredZombieVillagerTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
        }

        public boolean matches(LootContext pZombie, LootContext pVillager) {
            return this.zombie.isPresent() && !this.zombie.get().matches(pZombie)
                ? false
                : !this.villager.isPresent() || this.villager.get().matches(pVillager);
        }

        @Override
        public void serializeToJson(CriterionValidator p_309494_) {
            SimpleCriterionTrigger.SimpleInstance.super.serializeToJson(p_309494_);
            p_309494_.m_307484_(this.zombie, ".zombie");
            p_309494_.m_307484_(this.villager, ".villager");
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_302742_;
        }
    }
}
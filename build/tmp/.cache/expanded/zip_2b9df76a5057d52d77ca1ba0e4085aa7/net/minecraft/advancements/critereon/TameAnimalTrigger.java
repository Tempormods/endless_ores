package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.storage.loot.LootContext;

public class TameAnimalTrigger extends SimpleCriterionTrigger<TameAnimalTrigger.TriggerInstance> {
    @Override
    public Codec<TameAnimalTrigger.TriggerInstance> createInstance() {
        return TameAnimalTrigger.TriggerInstance.f_302578_;
    }

    public void trigger(ServerPlayer pPlayer, Animal pEntity) {
        LootContext lootcontext = EntityPredicate.createContext(pPlayer, pEntity);
        this.trigger(pPlayer, p_68838_ -> p_68838_.matches(lootcontext));
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> f_303748_, Optional<ContextAwarePredicate> entity)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TameAnimalTrigger.TriggerInstance> f_302578_ = RecordCodecBuilder.create(
            p_325254_ -> p_325254_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(TameAnimalTrigger.TriggerInstance::playerPredicate),
                        EntityPredicate.f_303210_.optionalFieldOf("entity").forGetter(TameAnimalTrigger.TriggerInstance::entity)
                    )
                    .apply(p_325254_, TameAnimalTrigger.TriggerInstance::new)
        );

        public static Criterion<TameAnimalTrigger.TriggerInstance> tamedAnimal() {
            return CriteriaTriggers.TAME_ANIMAL.createCriterion(new TameAnimalTrigger.TriggerInstance(Optional.empty(), Optional.empty()));
        }

        public static Criterion<TameAnimalTrigger.TriggerInstance> tamedAnimal(EntityPredicate.Builder pEntity) {
            return CriteriaTriggers.TAME_ANIMAL
                .createCriterion(new TameAnimalTrigger.TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(pEntity))));
        }

        public boolean matches(LootContext pLootContext) {
            return this.entity.isEmpty() || this.entity.get().matches(pLootContext);
        }

        @Override
        public void serializeToJson(CriterionValidator p_309538_) {
            SimpleCriterionTrigger.SimpleInstance.super.serializeToJson(p_309538_);
            p_309538_.m_307484_(this.entity, ".entity");
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_303748_;
        }
    }
}
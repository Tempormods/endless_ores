package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class PlayerInteractTrigger extends SimpleCriterionTrigger<PlayerInteractTrigger.TriggerInstance> {
    @Override
    public Codec<PlayerInteractTrigger.TriggerInstance> createInstance() {
        return PlayerInteractTrigger.TriggerInstance.f_303470_;
    }

    public void trigger(ServerPlayer pPlayer, ItemStack pItem, Entity pEntity) {
        LootContext lootcontext = EntityPredicate.createContext(pPlayer, pEntity);
        this.trigger(pPlayer, p_61501_ -> p_61501_.matches(pItem, lootcontext));
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> f_303250_, Optional<ItemPredicate> item, Optional<ContextAwarePredicate> entity)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<PlayerInteractTrigger.TriggerInstance> f_303470_ = RecordCodecBuilder.create(
            p_325239_ -> p_325239_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(PlayerInteractTrigger.TriggerInstance::playerPredicate),
                        ItemPredicate.CODEC.optionalFieldOf("item").forGetter(PlayerInteractTrigger.TriggerInstance::item),
                        EntityPredicate.f_303210_.optionalFieldOf("entity").forGetter(PlayerInteractTrigger.TriggerInstance::entity)
                    )
                    .apply(p_325239_, PlayerInteractTrigger.TriggerInstance::new)
        );

        public static Criterion<PlayerInteractTrigger.TriggerInstance> itemUsedOnEntity(
            Optional<ContextAwarePredicate> pPlayer, ItemPredicate.Builder pItem, Optional<ContextAwarePredicate> pEntity
        ) {
            return CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.createCriterion(new PlayerInteractTrigger.TriggerInstance(pPlayer, Optional.of(pItem.build()), pEntity));
        }

        public static Criterion<PlayerInteractTrigger.TriggerInstance> itemUsedOnEntity(ItemPredicate.Builder pItem, Optional<ContextAwarePredicate> pEntity) {
            return itemUsedOnEntity(Optional.empty(), pItem, pEntity);
        }

        public boolean matches(ItemStack pItem, LootContext pLootContext) {
            return this.item.isPresent() && !this.item.get().test(pItem)
                ? false
                : this.entity.isEmpty() || this.entity.get().matches(pLootContext);
        }

        @Override
        public void serializeToJson(CriterionValidator p_309953_) {
            SimpleCriterionTrigger.SimpleInstance.super.serializeToJson(p_309953_);
            p_309953_.m_307484_(this.entity, ".entity");
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_303250_;
        }
    }
}
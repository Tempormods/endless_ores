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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class PickedUpItemTrigger extends SimpleCriterionTrigger<PickedUpItemTrigger.TriggerInstance> {
    @Override
    public Codec<PickedUpItemTrigger.TriggerInstance> createInstance() {
        return PickedUpItemTrigger.TriggerInstance.f_302950_;
    }

    public void trigger(ServerPlayer pPlayer, ItemStack pStack, @Nullable Entity pEntity) {
        LootContext lootcontext = EntityPredicate.createContext(pPlayer, pEntity);
        this.trigger(pPlayer, p_221306_ -> p_221306_.matches(pPlayer, pStack, lootcontext));
    }

    public static record TriggerInstance(
        Optional<ContextAwarePredicate> f_302789_, Optional<ItemPredicate> item, Optional<ContextAwarePredicate> entity
    ) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<PickedUpItemTrigger.TriggerInstance> f_302950_ = RecordCodecBuilder.create(
            p_325237_ -> p_325237_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(PickedUpItemTrigger.TriggerInstance::playerPredicate),
                        ItemPredicate.CODEC.optionalFieldOf("item").forGetter(PickedUpItemTrigger.TriggerInstance::item),
                        EntityPredicate.f_303210_.optionalFieldOf("entity").forGetter(PickedUpItemTrigger.TriggerInstance::entity)
                    )
                    .apply(p_325237_, PickedUpItemTrigger.TriggerInstance::new)
        );

        public static Criterion<PickedUpItemTrigger.TriggerInstance> thrownItemPickedUpByEntity(
            ContextAwarePredicate pPlayer, Optional<ItemPredicate> pItem, Optional<ContextAwarePredicate> pEntity
        ) {
            return CriteriaTriggers.THROWN_ITEM_PICKED_UP_BY_ENTITY.createCriterion(new PickedUpItemTrigger.TriggerInstance(Optional.of(pPlayer), pItem, pEntity));
        }

        public static Criterion<PickedUpItemTrigger.TriggerInstance> thrownItemPickedUpByPlayer(
            Optional<ContextAwarePredicate> pPlayer, Optional<ItemPredicate> pItem, Optional<ContextAwarePredicate> pEntity
        ) {
            return CriteriaTriggers.THROWN_ITEM_PICKED_UP_BY_PLAYER.createCriterion(new PickedUpItemTrigger.TriggerInstance(pPlayer, pItem, pEntity));
        }

        public boolean matches(ServerPlayer pPlayer, ItemStack pStack, LootContext pContext) {
            return this.item.isPresent() && !this.item.get().test(pStack)
                ? false
                : !this.entity.isPresent() || this.entity.get().matches(pContext);
        }

        @Override
        public void serializeToJson(CriterionValidator p_311413_) {
            SimpleCriterionTrigger.SimpleInstance.super.serializeToJson(p_311413_);
            p_311413_.m_307484_(this.entity, ".entity");
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_302789_;
        }
    }
}
package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ConsumeItemTrigger extends SimpleCriterionTrigger<ConsumeItemTrigger.TriggerInstance> {
    @Override
    public Codec<ConsumeItemTrigger.TriggerInstance> createInstance() {
        return ConsumeItemTrigger.TriggerInstance.f_302482_;
    }

    public void trigger(ServerPlayer pPlayer, ItemStack pItem) {
        this.trigger(pPlayer, p_23687_ -> p_23687_.matches(pItem));
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> f_303553_, Optional<ItemPredicate> item)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<ConsumeItemTrigger.TriggerInstance> f_302482_ = RecordCodecBuilder.create(
            p_325197_ -> p_325197_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(ConsumeItemTrigger.TriggerInstance::playerPredicate),
                        ItemPredicate.CODEC.optionalFieldOf("item").forGetter(ConsumeItemTrigger.TriggerInstance::item)
                    )
                    .apply(p_325197_, ConsumeItemTrigger.TriggerInstance::new)
        );

        public static Criterion<ConsumeItemTrigger.TriggerInstance> usedItem() {
            return CriteriaTriggers.CONSUME_ITEM.createCriterion(new ConsumeItemTrigger.TriggerInstance(Optional.empty(), Optional.empty()));
        }

        public static Criterion<ConsumeItemTrigger.TriggerInstance> usedItem(ItemLike pItem) {
            return usedItem(ItemPredicate.Builder.item().of(pItem.asItem()));
        }

        public static Criterion<ConsumeItemTrigger.TriggerInstance> usedItem(ItemPredicate.Builder pItem) {
            return CriteriaTriggers.CONSUME_ITEM.createCriterion(new ConsumeItemTrigger.TriggerInstance(Optional.empty(), Optional.of(pItem.build())));
        }

        public boolean matches(ItemStack pItem) {
            return this.item.isEmpty() || this.item.get().test(pItem);
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_303553_;
        }
    }
}
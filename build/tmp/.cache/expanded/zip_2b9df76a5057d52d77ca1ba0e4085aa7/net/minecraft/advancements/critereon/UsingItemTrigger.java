package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class UsingItemTrigger extends SimpleCriterionTrigger<UsingItemTrigger.TriggerInstance> {
    @Override
    public Codec<UsingItemTrigger.TriggerInstance> createInstance() {
        return UsingItemTrigger.TriggerInstance.f_302252_;
    }

    public void trigger(ServerPlayer pPlayer, ItemStack pItem) {
        this.trigger(pPlayer, p_163870_ -> p_163870_.matches(pItem));
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> f_303433_, Optional<ItemPredicate> item)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<UsingItemTrigger.TriggerInstance> f_302252_ = RecordCodecBuilder.create(
            p_325259_ -> p_325259_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(UsingItemTrigger.TriggerInstance::playerPredicate),
                        ItemPredicate.CODEC.optionalFieldOf("item").forGetter(UsingItemTrigger.TriggerInstance::item)
                    )
                    .apply(p_325259_, UsingItemTrigger.TriggerInstance::new)
        );

        public static Criterion<UsingItemTrigger.TriggerInstance> lookingAt(EntityPredicate.Builder pPlayer, ItemPredicate.Builder pItem) {
            return CriteriaTriggers.USING_ITEM
                .createCriterion(new UsingItemTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(pPlayer)), Optional.of(pItem.build())));
        }

        public boolean matches(ItemStack pItem) {
            return !this.item.isPresent() || this.item.get().test(pItem);
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_303433_;
        }
    }
}
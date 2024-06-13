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

public class UsedTotemTrigger extends SimpleCriterionTrigger<UsedTotemTrigger.TriggerInstance> {
    @Override
    public Codec<UsedTotemTrigger.TriggerInstance> createInstance() {
        return UsedTotemTrigger.TriggerInstance.f_303672_;
    }

    public void trigger(ServerPlayer pPlayer, ItemStack pItem) {
        this.trigger(pPlayer, p_74436_ -> p_74436_.matches(pItem));
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> f_302542_, Optional<ItemPredicate> item)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<UsedTotemTrigger.TriggerInstance> f_303672_ = RecordCodecBuilder.create(
            p_325258_ -> p_325258_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(UsedTotemTrigger.TriggerInstance::playerPredicate),
                        ItemPredicate.CODEC.optionalFieldOf("item").forGetter(UsedTotemTrigger.TriggerInstance::item)
                    )
                    .apply(p_325258_, UsedTotemTrigger.TriggerInstance::new)
        );

        public static Criterion<UsedTotemTrigger.TriggerInstance> usedTotem(ItemPredicate pItem) {
            return CriteriaTriggers.USED_TOTEM.createCriterion(new UsedTotemTrigger.TriggerInstance(Optional.empty(), Optional.of(pItem)));
        }

        public static Criterion<UsedTotemTrigger.TriggerInstance> usedTotem(ItemLike pItem) {
            return CriteriaTriggers.USED_TOTEM
                .createCriterion(
                    new UsedTotemTrigger.TriggerInstance(Optional.empty(), Optional.of(ItemPredicate.Builder.item().of(pItem).build()))
                );
        }

        public boolean matches(ItemStack pItem) {
            return this.item.isEmpty() || this.item.get().test(pItem);
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_302542_;
        }
    }
}
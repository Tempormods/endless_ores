package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class EnchantedItemTrigger extends SimpleCriterionTrigger<EnchantedItemTrigger.TriggerInstance> {
    @Override
    public Codec<EnchantedItemTrigger.TriggerInstance> createInstance() {
        return EnchantedItemTrigger.TriggerInstance.f_302859_;
    }

    public void trigger(ServerPlayer pPlayer, ItemStack pItem, int pLevelsSpent) {
        this.trigger(pPlayer, p_27675_ -> p_27675_.matches(pItem, pLevelsSpent));
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> f_303838_, Optional<ItemPredicate> item, MinMaxBounds.Ints levels)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<EnchantedItemTrigger.TriggerInstance> f_302859_ = RecordCodecBuilder.create(
            p_325204_ -> p_325204_.group(
                        EntityPredicate.f_303210_.optionalFieldOf("player").forGetter(EnchantedItemTrigger.TriggerInstance::playerPredicate),
                        ItemPredicate.CODEC.optionalFieldOf("item").forGetter(EnchantedItemTrigger.TriggerInstance::item),
                        MinMaxBounds.Ints.CODEC
                            .optionalFieldOf("levels", MinMaxBounds.Ints.ANY)
                            .forGetter(EnchantedItemTrigger.TriggerInstance::levels)
                    )
                    .apply(p_325204_, EnchantedItemTrigger.TriggerInstance::new)
        );

        public static Criterion<EnchantedItemTrigger.TriggerInstance> enchantedItem() {
            return CriteriaTriggers.ENCHANTED_ITEM
                .createCriterion(new EnchantedItemTrigger.TriggerInstance(Optional.empty(), Optional.empty(), MinMaxBounds.Ints.ANY));
        }

        public boolean matches(ItemStack pItem, int pLevels) {
            return this.item.isPresent() && !this.item.get().test(pItem) ? false : this.levels.matches(pLevels);
        }

        @Override
        public Optional<ContextAwarePredicate> playerPredicate() {
            return this.f_303838_;
        }
    }
}
package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record ItemPredicate(
    Optional<HolderSet<Item>> items,
    MinMaxBounds.Ints count,
    DataComponentPredicate f_316810_,
    Map<ItemSubPredicate.Type<?>, ItemSubPredicate> f_315090_
) implements Predicate<ItemStack> {
    public static final Codec<ItemPredicate> CODEC = RecordCodecBuilder.create(
        p_325221_ -> p_325221_.group(
                    RegistryCodecs.homogeneousList(Registries.ITEM).optionalFieldOf("items").forGetter(ItemPredicate::items),
                    MinMaxBounds.Ints.CODEC.optionalFieldOf("count", MinMaxBounds.Ints.ANY).forGetter(ItemPredicate::count),
                    DataComponentPredicate.f_314199_.optionalFieldOf("components", DataComponentPredicate.f_314891_).forGetter(ItemPredicate::f_316810_),
                    ItemSubPredicate.f_313975_.optionalFieldOf("predicates", Map.of()).forGetter(ItemPredicate::f_315090_)
                )
                .apply(p_325221_, ItemPredicate::new)
    );

    public boolean test(ItemStack p_331873_) {
        if (this.items.isPresent() && !p_331873_.is(this.items.get())) {
            return false;
        } else if (!this.count.matches(p_331873_.getCount())) {
            return false;
        } else if (!this.f_316810_.m_323113_(p_331873_)) {
            return false;
        } else {
            for (ItemSubPredicate itemsubpredicate : this.f_315090_.values()) {
                if (!itemsubpredicate.m_321281_(p_331873_)) {
                    return false;
                }
            }

            return true;
        }
    }

    public static class Builder {
        private Optional<HolderSet<Item>> items = Optional.empty();
        private MinMaxBounds.Ints count = MinMaxBounds.Ints.ANY;
        private DataComponentPredicate f_314893_ = DataComponentPredicate.f_314891_;
        private final ImmutableMap.Builder<ItemSubPredicate.Type<?>, ItemSubPredicate> f_315904_ = ImmutableMap.builder();

        private Builder() {
        }

        public static ItemPredicate.Builder item() {
            return new ItemPredicate.Builder();
        }

        public ItemPredicate.Builder of(ItemLike... pItems) {
            this.items = Optional.of(HolderSet.direct(p_300947_ -> p_300947_.asItem().builtInRegistryHolder(), pItems));
            return this;
        }

        public ItemPredicate.Builder of(TagKey<Item> pTag) {
            this.items = Optional.of(BuiltInRegistries.ITEM.getOrCreateTag(pTag));
            return this;
        }

        public ItemPredicate.Builder withCount(MinMaxBounds.Ints pCount) {
            this.count = pCount;
            return this;
        }

        public <T extends ItemSubPredicate> ItemPredicate.Builder m_323078_(ItemSubPredicate.Type<T> p_331234_, T p_331877_) {
            this.f_315904_.put(p_331234_, p_331877_);
            return this;
        }

        public ItemPredicate.Builder m_324309_(DataComponentPredicate p_333545_) {
            this.f_314893_ = p_333545_;
            return this;
        }

        public ItemPredicate build() {
            return new ItemPredicate(this.items, this.count, this.f_314893_, this.f_315904_.build());
        }
    }
}
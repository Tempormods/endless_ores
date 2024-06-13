package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;

public record ItemBundlePredicate(Optional<CollectionPredicate<ItemStack, ItemPredicate>> f_316942_) implements SingleComponentItemPredicate<BundleContents> {
    public static final Codec<ItemBundlePredicate> f_314903_ = RecordCodecBuilder.create(
        p_333649_ -> p_333649_.group(
                    CollectionPredicate.<ItemStack, ItemPredicate>m_321514_(ItemPredicate.CODEC)
                        .optionalFieldOf("items")
                        .forGetter(ItemBundlePredicate::f_316942_)
                )
                .apply(p_333649_, ItemBundlePredicate::new)
    );

    @Override
    public DataComponentType<BundleContents> m_318698_() {
        return DataComponents.f_315394_;
    }

    public boolean m_318913_(ItemStack p_327929_, BundleContents p_336290_) {
        return !this.f_316942_.isPresent() || this.f_316942_.get().test(p_336290_.m_323607_());
    }
}
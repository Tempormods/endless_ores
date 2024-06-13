package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public record ItemContainerPredicate(Optional<CollectionPredicate<ItemStack, ItemPredicate>> f_317128_)
    implements SingleComponentItemPredicate<ItemContainerContents> {
    public static final Codec<ItemContainerPredicate> f_315175_ = RecordCodecBuilder.create(
        p_335794_ -> p_335794_.group(
                    CollectionPredicate.<ItemStack, ItemPredicate>m_321514_(ItemPredicate.CODEC)
                        .optionalFieldOf("items")
                        .forGetter(ItemContainerPredicate::f_317128_)
                )
                .apply(p_335794_, ItemContainerPredicate::new)
    );

    @Override
    public DataComponentType<ItemContainerContents> m_318698_() {
        return DataComponents.f_316065_;
    }

    public boolean m_318913_(ItemStack p_335095_, ItemContainerContents p_328970_) {
        return !this.f_317128_.isPresent() || this.f_317128_.get().test(p_328970_.m_318832_());
    }
}
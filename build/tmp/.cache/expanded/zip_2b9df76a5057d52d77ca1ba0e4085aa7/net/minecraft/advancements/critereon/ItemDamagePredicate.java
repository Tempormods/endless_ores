package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public record ItemDamagePredicate(MinMaxBounds.Ints f_316616_, MinMaxBounds.Ints f_314782_) implements SingleComponentItemPredicate<Integer> {
    public static final Codec<ItemDamagePredicate> f_314810_ = RecordCodecBuilder.create(
        p_331200_ -> p_331200_.group(
                    MinMaxBounds.Ints.CODEC.optionalFieldOf("durability", MinMaxBounds.Ints.ANY).forGetter(ItemDamagePredicate::f_316616_),
                    MinMaxBounds.Ints.CODEC.optionalFieldOf("damage", MinMaxBounds.Ints.ANY).forGetter(ItemDamagePredicate::f_314782_)
                )
                .apply(p_331200_, ItemDamagePredicate::new)
    );

    @Override
    public DataComponentType<Integer> m_318698_() {
        return DataComponents.f_313972_;
    }

    public boolean m_318913_(ItemStack p_335150_, Integer p_329961_) {
        return !this.f_316616_.matches(p_335150_.getMaxDamage() - p_329961_) ? false : this.f_314782_.matches(p_329961_);
    }

    public static ItemDamagePredicate m_320968_(MinMaxBounds.Ints p_335837_) {
        return new ItemDamagePredicate(p_335837_, MinMaxBounds.Ints.ANY);
    }
}
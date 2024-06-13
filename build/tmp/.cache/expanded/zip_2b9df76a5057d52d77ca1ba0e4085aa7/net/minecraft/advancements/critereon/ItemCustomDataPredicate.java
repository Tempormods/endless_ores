package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;

public record ItemCustomDataPredicate(NbtPredicate f_315116_) implements ItemSubPredicate {
    public static final Codec<ItemCustomDataPredicate> f_316481_ = NbtPredicate.CODEC
        .xmap(ItemCustomDataPredicate::new, ItemCustomDataPredicate::f_315116_);

    @Override
    public boolean m_321281_(ItemStack p_333399_) {
        return this.f_315116_.matches(p_333399_);
    }

    public static ItemCustomDataPredicate m_323143_(NbtPredicate p_329748_) {
        return new ItemCustomDataPredicate(p_329748_);
    }
}
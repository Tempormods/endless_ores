package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

public interface ItemSubPredicate {
    Codec<Map<ItemSubPredicate.Type<?>, ItemSubPredicate>> f_313975_ = Codec.dispatchedMap(
        BuiltInRegistries.f_315468_.byNameCodec(), ItemSubPredicate.Type::f_316804_
    );

    boolean m_321281_(ItemStack p_332513_);

    public static record Type<T extends ItemSubPredicate>(Codec<T> f_316804_) {
    }
}
package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public interface EnchantmentTags {
    TagKey<Enchantment> f_315440_ = m_323146_("tooltip_order");

    private static TagKey<Enchantment> m_323146_(String p_334094_) {
        return TagKey.create(Registries.ENCHANTMENT, new ResourceLocation("minecraft", p_334094_));
    }
}
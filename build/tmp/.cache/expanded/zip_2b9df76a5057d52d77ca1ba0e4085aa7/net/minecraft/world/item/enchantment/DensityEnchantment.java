package net.minecraft.world.item.enchantment;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

public class DensityEnchantment extends Enchantment {
    public DensityEnchantment() {
        super(
            Enchantment.m_319628_(
                ItemTags.f_314471_,
                10,
                5,
                Enchantment.m_318803_(1, 11),
                Enchantment.m_318803_(21, 11),
                1,
                FeatureFlagSet.of(FeatureFlags.f_302467_),
                EquipmentSlot.MAINHAND
            )
        );
    }

    public static float m_324546_(int p_334625_, float p_334740_) {
        return p_334740_ * (float)p_334625_;
    }
}
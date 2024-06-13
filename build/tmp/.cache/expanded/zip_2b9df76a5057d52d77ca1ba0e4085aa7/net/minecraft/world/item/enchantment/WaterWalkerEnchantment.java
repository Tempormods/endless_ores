package net.minecraft.world.item.enchantment;

public class WaterWalkerEnchantment extends Enchantment {
    public WaterWalkerEnchantment(Enchantment.EnchantmentDefinition p_333414_) {
        super(p_333414_);
    }

    @Override
    public boolean checkCompatibility(Enchantment pEnch) {
        return super.checkCompatibility(pEnch) && pEnch != Enchantments.FROST_WALKER;
    }
}
package net.minecraft.world.item.enchantment;

public class ArrowPiercingEnchantment extends Enchantment {
    public ArrowPiercingEnchantment(Enchantment.EnchantmentDefinition p_332974_) {
        super(p_332974_);
    }

    @Override
    public boolean checkCompatibility(Enchantment pEnch) {
        return super.checkCompatibility(pEnch) && pEnch != Enchantments.MULTISHOT;
    }
}
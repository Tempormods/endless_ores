package net.minecraft.world.item.enchantment;

public class MultiShotEnchantment extends Enchantment {
    public MultiShotEnchantment(Enchantment.EnchantmentDefinition p_334415_) {
        super(p_334415_);
    }

    @Override
    public boolean checkCompatibility(Enchantment pEnch) {
        return super.checkCompatibility(pEnch) && pEnch != Enchantments.PIERCING;
    }
}
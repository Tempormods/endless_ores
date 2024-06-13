package net.minecraft.world.item.enchantment;

public class UntouchingEnchantment extends Enchantment {
    protected UntouchingEnchantment(Enchantment.EnchantmentDefinition p_332403_) {
        super(p_332403_);
    }

    @Override
    public boolean checkCompatibility(Enchantment pEnch) {
        return super.checkCompatibility(pEnch) && pEnch != Enchantments.f_316753_;
    }
}
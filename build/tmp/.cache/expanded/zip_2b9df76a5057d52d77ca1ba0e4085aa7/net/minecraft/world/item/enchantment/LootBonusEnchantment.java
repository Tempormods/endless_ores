package net.minecraft.world.item.enchantment;

public class LootBonusEnchantment extends Enchantment {
    protected LootBonusEnchantment(Enchantment.EnchantmentDefinition p_333587_) {
        super(p_333587_);
    }

    @Override
    public boolean checkCompatibility(Enchantment pEnch) {
        return super.checkCompatibility(pEnch) && pEnch != Enchantments.SILK_TOUCH;
    }
}
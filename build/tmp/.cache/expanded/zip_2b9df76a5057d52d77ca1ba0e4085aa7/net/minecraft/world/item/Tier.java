package net.minecraft.world.item;

import java.util.List;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public interface Tier {
    int getUses();

    float getSpeed();

    float getAttackDamageBonus();

    TagKey<Block> getLevel();

    int getEnchantmentValue();

    Ingredient getRepairIngredient();

    default Tool m_323879_(TagKey<Block> p_331434_) {
        return new Tool(List.of(Tool.Rule.m_323695_(this.getLevel()), Tool.Rule.m_321972_(p_331434_, this.getSpeed())), 1.0F, 1);
    }
}
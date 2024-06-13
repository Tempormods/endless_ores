package net.minecraft.world.item;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public enum Tiers implements Tier {
    WOOD(BlockTags.f_315377_, 59, 2.0F, 0.0F, 15, () -> Ingredient.of(ItemTags.PLANKS)),
    STONE(BlockTags.f_315528_, 131, 4.0F, 1.0F, 5, () -> Ingredient.of(ItemTags.STONE_TOOL_MATERIALS)),
    IRON(BlockTags.f_315121_, 250, 6.0F, 2.0F, 14, () -> Ingredient.of(Items.IRON_INGOT)),
    DIAMOND(BlockTags.f_316248_, 1561, 8.0F, 3.0F, 10, () -> Ingredient.of(Items.DIAMOND)),
    GOLD(BlockTags.f_316327_, 32, 12.0F, 0.0F, 22, () -> Ingredient.of(Items.GOLD_INGOT)),
    NETHERITE(BlockTags.f_316762_, 2031, 9.0F, 4.0F, 15, () -> Ingredient.of(Items.NETHERITE_INGOT));

    private final TagKey<Block> f_316896_;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final Supplier<Ingredient> repairIngredient;

    private Tiers(
        final TagKey<Block> p_334032_, final int pLevel, final float pSpeed, final float pDamage, final int pUses, final Supplier<Ingredient> pRepairIngredient
    ) {
        this.f_316896_ = p_334032_;
        this.uses = pLevel;
        this.speed = pSpeed;
        this.damage = pDamage;
        this.enchantmentValue = pUses;
        this.repairIngredient = Suppliers.memoize(pRepairIngredient::get);
    }

    @Override
    public int getUses() {
        return this.uses;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.damage;
    }

    @Override
    public TagKey<Block> getLevel() {
        return this.f_316896_;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
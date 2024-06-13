package net.minecraft.world.item.crafting;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;

public class FireworkRocketRecipe extends CustomRecipe {
    private static final Ingredient PAPER_INGREDIENT = Ingredient.of(Items.PAPER);
    private static final Ingredient GUNPOWDER_INGREDIENT = Ingredient.of(Items.GUNPOWDER);
    private static final Ingredient STAR_INGREDIENT = Ingredient.of(Items.FIREWORK_STAR);

    public FireworkRocketRecipe(CraftingBookCategory pCategory) {
        super(pCategory);
    }

    public boolean matches(CraftingContainer pInv, Level pLevel) {
        boolean flag = false;
        int i = 0;

        for (int j = 0; j < pInv.getContainerSize(); j++) {
            ItemStack itemstack = pInv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (PAPER_INGREDIENT.test(itemstack)) {
                    if (flag) {
                        return false;
                    }

                    flag = true;
                } else if (GUNPOWDER_INGREDIENT.test(itemstack)) {
                    if (++i > 3) {
                        return false;
                    }
                } else if (!STAR_INGREDIENT.test(itemstack)) {
                    return false;
                }
            }
        }

        return flag && i >= 1;
    }

    public ItemStack assemble(CraftingContainer p_328326_, HolderLookup.Provider p_335679_) {
        List<FireworkExplosion> list = new ArrayList<>();
        int i = 0;

        for (int j = 0; j < p_328326_.getContainerSize(); j++) {
            ItemStack itemstack = p_328326_.getItem(j);
            if (!itemstack.isEmpty()) {
                if (GUNPOWDER_INGREDIENT.test(itemstack)) {
                    i++;
                } else if (STAR_INGREDIENT.test(itemstack)) {
                    FireworkExplosion fireworkexplosion = itemstack.m_323252_(DataComponents.f_315608_);
                    if (fireworkexplosion != null) {
                        list.add(fireworkexplosion);
                    }
                }
            }
        }

        ItemStack itemstack1 = new ItemStack(Items.FIREWORK_ROCKET, 3);
        itemstack1.m_322496_(DataComponents.f_316632_, new Fireworks(i, list));
        return itemstack1;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider p_333278_) {
        return new ItemStack(Items.FIREWORK_ROCKET);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_ROCKET;
    }
}
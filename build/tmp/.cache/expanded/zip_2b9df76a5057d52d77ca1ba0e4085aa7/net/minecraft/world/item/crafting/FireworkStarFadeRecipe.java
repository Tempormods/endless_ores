package net.minecraft.world.item.crafting;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.level.Level;

public class FireworkStarFadeRecipe extends CustomRecipe {
    private static final Ingredient STAR_INGREDIENT = Ingredient.of(Items.FIREWORK_STAR);

    public FireworkStarFadeRecipe(CraftingBookCategory pCategory) {
        super(pCategory);
    }

    public boolean matches(CraftingContainer pInv, Level pLevel) {
        boolean flag = false;
        boolean flag1 = false;

        for (int i = 0; i < pInv.getContainerSize(); i++) {
            ItemStack itemstack = pInv.getItem(i);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() instanceof DyeItem) {
                    flag = true;
                } else {
                    if (!STAR_INGREDIENT.test(itemstack)) {
                        return false;
                    }

                    if (flag1) {
                        return false;
                    }

                    flag1 = true;
                }
            }
        }

        return flag1 && flag;
    }

    public ItemStack assemble(CraftingContainer pContainer, HolderLookup.Provider p_333582_) {
        IntList intlist = new IntArrayList();
        ItemStack itemstack = null;

        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack itemstack1 = pContainer.getItem(i);
            Item item = itemstack1.getItem();
            if (item instanceof DyeItem) {
                intlist.add(((DyeItem)item).getDyeColor().getFireworkColor());
            } else if (STAR_INGREDIENT.test(itemstack1)) {
                itemstack = itemstack1.copyWithCount(1);
            }
        }

        if (itemstack != null && !intlist.isEmpty()) {
            itemstack.m_324919_(DataComponents.f_315608_, FireworkExplosion.f_316800_, intlist, FireworkExplosion::m_319637_);
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_STAR_FADE;
    }
}
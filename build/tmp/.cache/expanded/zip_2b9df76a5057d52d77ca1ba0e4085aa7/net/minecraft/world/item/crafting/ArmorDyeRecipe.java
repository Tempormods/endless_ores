package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;

public class ArmorDyeRecipe extends CustomRecipe {
    public ArmorDyeRecipe(CraftingBookCategory pCategory) {
        super(pCategory);
    }

    public boolean matches(CraftingContainer pInv, Level pLevel) {
        ItemStack itemstack = ItemStack.EMPTY;
        List<ItemStack> list = Lists.newArrayList();

        for (int i = 0; i < pInv.getContainerSize(); i++) {
            ItemStack itemstack1 = pInv.getItem(i);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.is(ItemTags.f_314020_)) {
                    if (!itemstack.isEmpty()) {
                        return false;
                    }

                    itemstack = itemstack1;
                } else {
                    if (!(itemstack1.getItem() instanceof DyeItem)) {
                        return false;
                    }

                    list.add(itemstack1);
                }
            }
        }

        return !itemstack.isEmpty() && !list.isEmpty();
    }

    public ItemStack assemble(CraftingContainer pContainer, HolderLookup.Provider p_329480_) {
        List<DyeItem> list = Lists.newArrayList();
        ItemStack itemstack = ItemStack.EMPTY;

        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack itemstack1 = pContainer.getItem(i);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.is(ItemTags.f_314020_)) {
                    if (!itemstack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    itemstack = itemstack1.copy();
                } else {
                    if (!(itemstack1.getItem() instanceof DyeItem dyeitem)) {
                        return ItemStack.EMPTY;
                    }

                    list.add(dyeitem);
                }
            }
        }

        return !itemstack.isEmpty() && !list.isEmpty() ? DyedItemColor.m_323436_(itemstack, list) : ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.ARMOR_DYE;
    }
}
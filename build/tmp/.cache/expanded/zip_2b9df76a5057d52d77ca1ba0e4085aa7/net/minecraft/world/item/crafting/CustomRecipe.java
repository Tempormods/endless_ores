package net.minecraft.world.item.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

public abstract class CustomRecipe implements CraftingRecipe {
    private final CraftingBookCategory category;

    public CustomRecipe(CraftingBookCategory pCategory) {
        this.category = pCategory;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider p_329886_) {
        return ItemStack.EMPTY;
    }

    @Override
    public CraftingBookCategory category() {
        return this.category;
    }
}
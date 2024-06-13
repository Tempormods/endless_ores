package net.minecraft.world.item.crafting;

import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.MapPostProcessing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapExtendingRecipe extends ShapedRecipe {
    public MapExtendingRecipe(CraftingBookCategory pCategory) {
        super(
            "",
            pCategory,
            ShapedRecipePattern.m_304825_(Map.of('#', Ingredient.of(Items.PAPER), 'x', Ingredient.of(Items.FILLED_MAP)), "###", "#x#", "###"),
            new ItemStack(Items.MAP)
        );
    }

    @Override
    public boolean matches(CraftingContainer pInv, Level pLevel) {
        if (!super.matches(pInv, pLevel)) {
            return false;
        } else {
            ItemStack itemstack = findFilledMap(pInv);
            if (itemstack.isEmpty()) {
                return false;
            } else {
                MapItemSavedData mapitemsaveddata = MapItem.getSavedData(itemstack, pLevel);
                if (mapitemsaveddata == null) {
                    return false;
                } else {
                    return mapitemsaveddata.isExplorationMap() ? false : mapitemsaveddata.scale < 4;
                }
            }
        }
    }

    @Override
    public ItemStack assemble(CraftingContainer pContainer, HolderLookup.Provider p_329331_) {
        ItemStack itemstack = findFilledMap(pContainer).copyWithCount(1);
        itemstack.m_322496_(DataComponents.f_316856_, MapPostProcessing.SCALE);
        return itemstack;
    }

    private static ItemStack findFilledMap(CraftingContainer pContainer) {
        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack itemstack = pContainer.getItem(i);
            if (itemstack.is(Items.FILLED_MAP)) {
                return itemstack;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.MAP_EXTENDING;
    }
}
package net.minecraft.world.item.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class BannerDuplicateRecipe extends CustomRecipe {
    public BannerDuplicateRecipe(CraftingBookCategory pCategory) {
        super(pCategory);
    }

    public boolean matches(CraftingContainer pInv, Level pLevel) {
        DyeColor dyecolor = null;
        ItemStack itemstack = null;
        ItemStack itemstack1 = null;

        for (int i = 0; i < pInv.getContainerSize(); i++) {
            ItemStack itemstack2 = pInv.getItem(i);
            if (!itemstack2.isEmpty()) {
                Item item = itemstack2.getItem();
                if (!(item instanceof BannerItem)) {
                    return false;
                }

                BannerItem banneritem = (BannerItem)item;
                if (dyecolor == null) {
                    dyecolor = banneritem.getColor();
                } else if (dyecolor != banneritem.getColor()) {
                    return false;
                }

                int j = itemstack2.m_322304_(DataComponents.f_314522_, BannerPatternLayers.f_316086_).f_315710_().size();
                if (j > 6) {
                    return false;
                }

                if (j > 0) {
                    if (itemstack != null) {
                        return false;
                    }

                    itemstack = itemstack2;
                } else {
                    if (itemstack1 != null) {
                        return false;
                    }

                    itemstack1 = itemstack2;
                }
            }
        }

        return itemstack != null && itemstack1 != null;
    }

    public ItemStack assemble(CraftingContainer p_335631_, HolderLookup.Provider p_333234_) {
        for (int i = 0; i < p_335631_.getContainerSize(); i++) {
            ItemStack itemstack = p_335631_.getItem(i);
            if (!itemstack.isEmpty()) {
                int j = itemstack.m_322304_(DataComponents.f_314522_, BannerPatternLayers.f_316086_).f_315710_().size();
                if (j > 0 && j <= 6) {
                    return itemstack.copyWithCount(1);
                }
            }
        }

        return ItemStack.EMPTY;
    }

    public NonNullList<ItemStack> getRemainingItems(CraftingContainer pInv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(pInv.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); i++) {
            ItemStack itemstack = pInv.getItem(i);
            if (!itemstack.isEmpty()) {
                if (itemstack.hasCraftingRemainingItem()) {
                    nonnulllist.set(i, itemstack.getCraftingRemainingItem());
                } else if (!itemstack.m_322304_(DataComponents.f_314522_, BannerPatternLayers.f_316086_).f_315710_().isEmpty()) {
                    nonnulllist.set(i, itemstack.copyWithCount(1));
                }
            }
        }

        return nonnulllist;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.BANNER_DUPLICATE;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }
}

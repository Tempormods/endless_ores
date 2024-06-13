package net.minecraft.world.item.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class ShieldDecorationRecipe extends CustomRecipe {
    public ShieldDecorationRecipe(CraftingBookCategory pCategory) {
        super(pCategory);
    }

    public boolean matches(CraftingContainer pInv, Level pLevel) {
        ItemStack itemstack = ItemStack.EMPTY;
        ItemStack itemstack1 = ItemStack.EMPTY;

        for (int i = 0; i < pInv.getContainerSize(); i++) {
            ItemStack itemstack2 = pInv.getItem(i);
            if (!itemstack2.isEmpty()) {
                if (itemstack2.getItem() instanceof BannerItem) {
                    if (!itemstack1.isEmpty()) {
                        return false;
                    }

                    itemstack1 = itemstack2;
                } else {
                    if (!itemstack2.is(Items.SHIELD)) {
                        return false;
                    }

                    if (!itemstack.isEmpty()) {
                        return false;
                    }

                    BannerPatternLayers bannerpatternlayers = itemstack2.m_322304_(DataComponents.f_314522_, BannerPatternLayers.f_316086_);
                    if (!bannerpatternlayers.f_315710_().isEmpty()) {
                        return false;
                    }

                    itemstack = itemstack2;
                }
            }
        }

        return !itemstack.isEmpty() && !itemstack1.isEmpty();
    }

    public ItemStack assemble(CraftingContainer pContainer, HolderLookup.Provider p_330479_) {
        ItemStack itemstack = ItemStack.EMPTY;
        ItemStack itemstack1 = ItemStack.EMPTY;

        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack itemstack2 = pContainer.getItem(i);
            if (!itemstack2.isEmpty()) {
                if (itemstack2.getItem() instanceof BannerItem) {
                    itemstack = itemstack2;
                } else if (itemstack2.is(Items.SHIELD)) {
                    itemstack1 = itemstack2.copy();
                }
            }
        }

        if (itemstack1.isEmpty()) {
            return itemstack1;
        } else {
            itemstack1.m_322496_(DataComponents.f_314522_, itemstack.m_323252_(DataComponents.f_314522_));
            itemstack1.m_322496_(DataComponents.f_315952_, ((BannerItem)itemstack.getItem()).getColor());
            return itemstack1;
        }
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHIELD_DECORATION;
    }
}
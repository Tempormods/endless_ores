package net.minecraft.world.item.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.Level;

public class BookCloningRecipe extends CustomRecipe {
    public BookCloningRecipe(CraftingBookCategory pCategory) {
        super(pCategory);
    }

    public boolean matches(CraftingContainer pInv, Level pLevel) {
        int i = 0;
        ItemStack itemstack = ItemStack.EMPTY;

        for (int j = 0; j < pInv.getContainerSize(); j++) {
            ItemStack itemstack1 = pInv.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.is(Items.WRITTEN_BOOK)) {
                    if (!itemstack.isEmpty()) {
                        return false;
                    }

                    itemstack = itemstack1;
                } else {
                    if (!itemstack1.is(Items.WRITABLE_BOOK)) {
                        return false;
                    }

                    i++;
                }
            }
        }

        return !itemstack.isEmpty() && i > 0;
    }

    public ItemStack assemble(CraftingContainer pContainer, HolderLookup.Provider p_327928_) {
        int i = 0;
        ItemStack itemstack = ItemStack.EMPTY;

        for (int j = 0; j < pContainer.getContainerSize(); j++) {
            ItemStack itemstack1 = pContainer.getItem(j);
            if (!itemstack1.isEmpty()) {
                if (itemstack1.is(Items.WRITTEN_BOOK)) {
                    if (!itemstack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    itemstack = itemstack1;
                } else {
                    if (!itemstack1.is(Items.WRITABLE_BOOK)) {
                        return ItemStack.EMPTY;
                    }

                    i++;
                }
            }
        }

        WrittenBookContent writtenbookcontent = itemstack.m_323252_(DataComponents.f_315840_);
        if (!itemstack.isEmpty() && i >= 1 && writtenbookcontent != null) {
            WrittenBookContent writtenbookcontent1 = writtenbookcontent.m_319355_();
            if (writtenbookcontent1 == null) {
                return ItemStack.EMPTY;
            } else {
                ItemStack itemstack2 = itemstack.copyWithCount(i);
                itemstack2.m_322496_(DataComponents.f_315840_, writtenbookcontent1);
                return itemstack2;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    public NonNullList<ItemStack> getRemainingItems(CraftingContainer pInv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(pInv.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); i++) {
            ItemStack itemstack = pInv.getItem(i);
            if (itemstack.hasCraftingRemainingItem()) {
                nonnulllist.set(i, itemstack.getCraftingRemainingItem());
            } else if (itemstack.getItem() instanceof WrittenBookItem) {
                nonnulllist.set(i, itemstack.copyWithCount(1));
                break;
            }
        }

        return nonnulllist;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.BOOK_CLONING;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth >= 3 && pHeight >= 3;
    }
}

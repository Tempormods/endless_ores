package net.minecraft.world.item.crafting;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public class RepairItemRecipe extends CustomRecipe {
    public RepairItemRecipe(CraftingBookCategory pCategory) {
        super(pCategory);
    }

    @Nullable
    private Pair<ItemStack, ItemStack> m_321187_(CraftingContainer p_335773_) {
        ItemStack itemstack = null;
        ItemStack itemstack1 = null;

        for (int i = 0; i < p_335773_.getContainerSize(); i++) {
            ItemStack itemstack2 = p_335773_.getItem(i);
            if (!itemstack2.isEmpty()) {
                if (itemstack == null) {
                    itemstack = itemstack2;
                } else {
                    if (itemstack1 != null) {
                        return null;
                    }

                    itemstack1 = itemstack2;
                }
            }
        }

        return itemstack != null && itemstack1 != null && m_319510_(itemstack, itemstack1) ? Pair.of(itemstack, itemstack1) : null;
    }

    private static boolean m_319510_(ItemStack p_335534_, ItemStack p_329259_) {
        return p_329259_.is(p_335534_.getItem())
            && p_335534_.getCount() == 1
            && p_329259_.getCount() == 1
            && p_335534_.m_319951_(DataComponents.f_316415_)
            && p_329259_.m_319951_(DataComponents.f_316415_)
            && p_335534_.m_319951_(DataComponents.f_313972_)
            && p_329259_.m_319951_(DataComponents.f_313972_);
    }

    public boolean matches(CraftingContainer pInv, Level pLevel) {
        return this.m_321187_(pInv) != null;
    }

    public ItemStack assemble(CraftingContainer p_333623_, HolderLookup.Provider p_331714_) {
        Pair<ItemStack, ItemStack> pair = this.m_321187_(p_333623_);
        if (pair == null) {
            return ItemStack.EMPTY;
        } else {
            ItemStack itemstack = pair.getFirst();
            ItemStack itemstack1 = pair.getSecond();
            int i = Math.max(itemstack.getMaxDamage(), itemstack1.getMaxDamage());
            int j = itemstack.getMaxDamage() - itemstack.getDamageValue();
            int k = itemstack1.getMaxDamage() - itemstack1.getDamageValue();
            int l = j + k + i * 5 / 100;
            ItemStack itemstack2 = new ItemStack(itemstack.getItem());
            itemstack2.m_322496_(DataComponents.f_316415_, i);
            itemstack2.setDamageValue(Math.max(i - l, 0));
            ItemEnchantments itemenchantments = EnchantmentHelper.m_324152_(itemstack);
            ItemEnchantments itemenchantments1 = EnchantmentHelper.m_324152_(itemstack1);
            EnchantmentHelper.m_320959_(
                itemstack2,
                p_327207_ -> p_331714_.lookupOrThrow(Registries.ENCHANTMENT).listElements().map(Holder::value).filter(Enchantment::isCurse).forEach(p_327203_ -> {
                        int i1 = Math.max(itemenchantments.m_320299_(p_327203_), itemenchantments1.m_320299_(p_327203_));
                        if (i1 > 0) {
                            p_327207_.m_323014_(p_327203_, i1);
                        }
                    })
            );
            return itemstack2;
        }
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.REPAIR_ITEM;
    }
}
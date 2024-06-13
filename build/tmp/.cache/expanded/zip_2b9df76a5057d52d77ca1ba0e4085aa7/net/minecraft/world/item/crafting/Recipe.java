package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public interface Recipe<C extends Container> {
    Codec<Recipe<?>> f_302387_ = BuiltInRegistries.RECIPE_SERIALIZER.byNameCodec().dispatch(Recipe::getSerializer, RecipeSerializer::codec);
    StreamCodec<RegistryFriendlyByteBuf, Recipe<?>> f_315202_ = ByteBufCodecs.m_320159_(Registries.RECIPE_SERIALIZER)
        .m_321818_(Recipe::getSerializer, RecipeSerializer::m_318841_);

    boolean matches(C pContainer, Level pLevel);

    ItemStack assemble(C pContainer, HolderLookup.Provider p_332698_);

    boolean canCraftInDimensions(int pWidth, int pHeight);

    ItemStack getResultItem(HolderLookup.Provider p_331967_);

    default NonNullList<ItemStack> getRemainingItems(C pContainer) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(pContainer.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); i++) {
            ItemStack item = pContainer.getItem(i);
            if (item.hasCraftingRemainingItem()) {
                nonnulllist.set(i, item.getCraftingRemainingItem());
            }
        }

        return nonnulllist;
    }

    default NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }

    default boolean isSpecial() {
        return false;
    }

    default boolean showNotification() {
        return true;
    }

    default String getGroup() {
        return "";
    }

    default ItemStack getToastSymbol() {
        return new ItemStack(Blocks.CRAFTING_TABLE);
    }

    RecipeSerializer<?> getSerializer();

    RecipeType<?> getType();

    default boolean isIncomplete() {
        NonNullList<Ingredient> nonnulllist = this.getIngredients();
        return nonnulllist.isEmpty() || nonnulllist.stream().anyMatch(net.minecraftforge.common.ForgeHooks::hasNoElements);
    }
}

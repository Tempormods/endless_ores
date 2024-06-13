package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class SimpleCraftingRecipeSerializer<T extends CraftingRecipe> implements RecipeSerializer<T> {
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> f_316295_;

    public SimpleCraftingRecipeSerializer(SimpleCraftingRecipeSerializer.Factory<T> pConstructor) {
        this.codec = RecordCodecBuilder.mapCodec(
            p_309259_ -> p_309259_.group(
                        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CraftingRecipe::category)
                    )
                    .apply(p_309259_, pConstructor::create)
        );
        this.f_316295_ = StreamCodec.m_322204_(CraftingBookCategory.f_315540_, CraftingRecipe::category, pConstructor::create);
    }

    @Override
    public MapCodec<T> codec() {
        return this.codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> m_318841_() {
        return this.f_316295_;
    }

    @FunctionalInterface
    public interface Factory<T extends CraftingRecipe> {
        T create(CraftingBookCategory pCategory);
    }
}
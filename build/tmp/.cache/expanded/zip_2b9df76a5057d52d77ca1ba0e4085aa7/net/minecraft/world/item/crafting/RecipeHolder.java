package net.minecraft.world.item.crafting;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record RecipeHolder<T extends Recipe<?>>(ResourceLocation id, T value) {
    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeHolder<?>> f_314856_ = StreamCodec.m_320349_(
        ResourceLocation.f_314488_, RecipeHolder::id, Recipe.f_315202_, RecipeHolder::value, RecipeHolder::new
    );

    @Override
    public boolean equals(Object pOther) {
        if (this == pOther) {
            return true;
        } else {
            if (pOther instanceof RecipeHolder<?> recipeholder && this.id.equals(recipeholder.id)) {
                return true;
            }

            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
}
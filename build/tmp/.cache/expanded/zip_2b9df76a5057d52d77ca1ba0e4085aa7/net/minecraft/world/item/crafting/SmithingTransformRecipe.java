package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SmithingTransformRecipe implements SmithingRecipe {
    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;

    public SmithingTransformRecipe(Ingredient pTemplate, Ingredient pBase, Ingredient pAddition, ItemStack pResult) {
        this.template = pTemplate;
        this.base = pBase;
        this.addition = pAddition;
        this.result = pResult;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return this.template.test(pContainer.getItem(0)) && this.base.test(pContainer.getItem(1)) && this.addition.test(pContainer.getItem(2));
    }

    @Override
    public ItemStack assemble(Container pContainer, HolderLookup.Provider p_331030_) {
        ItemStack itemstack = pContainer.getItem(1).m_319323_(this.result.getItem(), this.result.getCount());
        itemstack.m_319238_(this.result.m_324277_());
        return itemstack;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider p_330801_) {
        return this.result;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack pStack) {
        return this.template.test(pStack);
    }

    @Override
    public boolean isBaseIngredient(ItemStack pStack) {
        return this.base.test(pStack);
    }

    @Override
    public boolean isAdditionIngredient(ItemStack pStack) {
        return this.addition.test(pStack);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SMITHING_TRANSFORM;
    }

    @Override
    public boolean isIncomplete() {
        return Stream.of(this.template, this.base, this.addition).anyMatch(net.minecraftforge.common.ForgeHooks::hasNoElements);
    }

    public static class Serializer implements RecipeSerializer<SmithingTransformRecipe> {
        private static final MapCodec<SmithingTransformRecipe> CODEC = RecordCodecBuilder.mapCodec(
            p_327220_ -> p_327220_.group(
                        Ingredient.CODEC.fieldOf("template").forGetter(p_297231_ -> p_297231_.template),
                        Ingredient.CODEC.fieldOf("base").forGetter(p_298250_ -> p_298250_.base),
                        Ingredient.CODEC.fieldOf("addition").forGetter(p_299654_ -> p_299654_.addition),
                        ItemStack.f_315780_.fieldOf("result").forGetter(p_297480_ -> p_297480_.result)
                    )
                    .apply(p_327220_, SmithingTransformRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, SmithingTransformRecipe> f_313917_ = StreamCodec.m_320617_(
            SmithingTransformRecipe.Serializer::m_266565_, SmithingTransformRecipe.Serializer::m_266366_
        );

        @Override
        public MapCodec<SmithingTransformRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SmithingTransformRecipe> m_318841_() {
            return f_313917_;
        }

        private static SmithingTransformRecipe m_266366_(RegistryFriendlyByteBuf p_333917_) {
            Ingredient ingredient = Ingredient.f_317040_.m_318688_(p_333917_);
            Ingredient ingredient1 = Ingredient.f_317040_.m_318688_(p_333917_);
            Ingredient ingredient2 = Ingredient.f_317040_.m_318688_(p_333917_);
            ItemStack itemstack = ItemStack.f_315801_.m_318688_(p_333917_);
            return new SmithingTransformRecipe(ingredient, ingredient1, ingredient2, itemstack);
        }

        private static void m_266565_(RegistryFriendlyByteBuf p_329920_, SmithingTransformRecipe p_266927_) {
            Ingredient.f_317040_.m_318638_(p_329920_, p_266927_.template);
            Ingredient.f_317040_.m_318638_(p_329920_, p_266927_.base);
            Ingredient.f_317040_.m_318638_(p_329920_, p_266927_.addition);
            ItemStack.f_315801_.m_318638_(p_329920_, p_266927_.result);
        }
    }
}

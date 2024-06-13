package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.level.Level;

public class SmithingTrimRecipe implements SmithingRecipe {
    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;

    public SmithingTrimRecipe(Ingredient pTemplate, Ingredient pBase, Ingredient pAddition) {
        this.template = pTemplate;
        this.base = pBase;
        this.addition = pAddition;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return this.template.test(pContainer.getItem(0)) && this.base.test(pContainer.getItem(1)) && this.addition.test(pContainer.getItem(2));
    }

    @Override
    public ItemStack assemble(Container p_334062_, HolderLookup.Provider p_330268_) {
        ItemStack itemstack = p_334062_.getItem(1);
        if (this.base.test(itemstack)) {
            Optional<Holder.Reference<TrimMaterial>> optional = TrimMaterials.getFromIngredient(p_330268_, p_334062_.getItem(2));
            Optional<Holder.Reference<TrimPattern>> optional1 = TrimPatterns.getFromTemplate(p_330268_, p_334062_.getItem(0));
            if (optional.isPresent() && optional1.isPresent()) {
                ArmorTrim armortrim = itemstack.m_323252_(DataComponents.f_315199_);
                if (armortrim != null && armortrim.hasPatternAndMaterial(optional1.get(), optional.get())) {
                    return ItemStack.EMPTY;
                }

                ItemStack itemstack1 = itemstack.copyWithCount(1);
                itemstack1.m_322496_(DataComponents.f_315199_, new ArmorTrim(optional.get(), optional1.get()));
                return itemstack1;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider p_335603_) {
        ItemStack itemstack = new ItemStack(Items.IRON_CHESTPLATE);
        Optional<Holder.Reference<TrimPattern>> optional = p_335603_.lookupOrThrow(Registries.TRIM_PATTERN).listElements().findFirst();
        Optional<Holder.Reference<TrimMaterial>> optional1 = p_335603_.lookupOrThrow(Registries.TRIM_MATERIAL).m_254926_(TrimMaterials.REDSTONE);
        if (optional.isPresent() && optional1.isPresent()) {
            itemstack.m_322496_(DataComponents.f_315199_, new ArmorTrim(optional1.get(), optional.get()));
        }

        return itemstack;
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
        return RecipeSerializer.SMITHING_TRIM;
    }

    @Override
    public boolean isIncomplete() {
        return Stream.of(this.template, this.base, this.addition).anyMatch(net.minecraftforge.common.ForgeHooks::hasNoElements);
    }

    public static class Serializer implements RecipeSerializer<SmithingTrimRecipe> {
        private static final MapCodec<SmithingTrimRecipe> CODEC = RecordCodecBuilder.mapCodec(
            p_297394_ -> p_297394_.group(
                        Ingredient.CODEC.fieldOf("template").forGetter(p_298441_ -> p_298441_.template),
                        Ingredient.CODEC.fieldOf("base").forGetter(p_297838_ -> p_297838_.base),
                        Ingredient.CODEC.fieldOf("addition").forGetter(p_299309_ -> p_299309_.addition)
                    )
                    .apply(p_297394_, SmithingTrimRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, SmithingTrimRecipe> f_314618_ = StreamCodec.m_320617_(
            SmithingTrimRecipe.Serializer::m_266337_, SmithingTrimRecipe.Serializer::m_266515_
        );

        @Override
        public MapCodec<SmithingTrimRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SmithingTrimRecipe> m_318841_() {
            return f_314618_;
        }

        private static SmithingTrimRecipe m_266515_(RegistryFriendlyByteBuf p_333367_) {
            Ingredient ingredient = Ingredient.f_317040_.m_318688_(p_333367_);
            Ingredient ingredient1 = Ingredient.f_317040_.m_318688_(p_333367_);
            Ingredient ingredient2 = Ingredient.f_317040_.m_318688_(p_333367_);
            return new SmithingTrimRecipe(ingredient, ingredient1, ingredient2);
        }

        private static void m_266337_(RegistryFriendlyByteBuf p_335485_, SmithingTrimRecipe p_335201_) {
            Ingredient.f_317040_.m_318638_(p_335485_, p_335201_.template);
            Ingredient.f_317040_.m_318638_(p_335485_, p_335201_.base);
            Ingredient.f_317040_.m_318638_(p_335485_, p_335201_.addition);
        }
    }
}

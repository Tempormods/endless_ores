package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ShapelessRecipe implements CraftingRecipe {
    final String group;
    final CraftingBookCategory category;
    final ItemStack result;
    final NonNullList<Ingredient> ingredients;
    private final boolean isSimple;

    public ShapelessRecipe(String pGroup, CraftingBookCategory pCategory, ItemStack pResult, NonNullList<Ingredient> pIngredients) {
        this.group = pGroup;
        this.category = pCategory;
        this.result = pResult;
        this.ingredients = pIngredients;
        this.isSimple = pIngredients.stream().allMatch(Ingredient::isSimple);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHAPELESS_RECIPE;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public CraftingBookCategory category() {
        return this.category;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider p_336057_) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public boolean matches(CraftingContainer pInv, Level pLevel) {
        StackedContents stackedcontents = new StackedContents();
        var inputs = new java.util.ArrayList<ItemStack>();
        int i = 0;

        for (int j = 0; j < pInv.getContainerSize(); j++) {
            ItemStack itemstack = pInv.getItem(j);
            if (!itemstack.isEmpty()) {
                i++;
                if (isSimple)
                stackedcontents.accountStack(itemstack, 1);
                else inputs.add(itemstack);
            }
        }

        return i == this.ingredients.size() && (isSimple ? stackedcontents.canCraft(this, null) : net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs,  this.ingredients) != null);
    }

    public ItemStack assemble(CraftingContainer pContainer, HolderLookup.Provider p_334364_) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= this.ingredients.size();
    }

    public static class Serializer implements RecipeSerializer<ShapelessRecipe> {
        private static final MapCodec<ShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(
            p_327212_ -> p_327212_.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(p_299460_ -> p_299460_.group),
                        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(p_297437_ -> p_297437_.category),
                        ItemStack.f_315780_.fieldOf("result").forGetter(p_300770_ -> p_300770_.result),
                        Ingredient.CODEC_NONEMPTY
                            .listOf()
                            .fieldOf("ingredients")
                            .flatXmap(
                                p_297969_ -> {
                                    Ingredient[] aingredient = p_297969_.stream().filter(p_298915_ -> !p_298915_.isEmpty()).toArray(Ingredient[]::new);
                                    if (aingredient.length == 0) {
                                        return DataResult.error(() -> "No ingredients for shapeless recipe");
                                    } else {
                                        return aingredient.length > ShapedRecipe.MAX_WIDTH * ShapedRecipe.MAX_HEIGHT
                                            ? DataResult.error(() -> "Too many ingredients for shapeless recipe")
                                            : DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                                    }
                                },
                                DataResult::success
                            )
                            .forGetter(p_298509_ -> p_298509_.ingredients)
                    )
                    .apply(p_327212_, ShapelessRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessRecipe> f_316453_ = StreamCodec.m_320617_(
            ShapelessRecipe.Serializer::m_44280_, ShapelessRecipe.Serializer::m_44292_
        );

        @Override
        public MapCodec<ShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShapelessRecipe> m_318841_() {
            return f_316453_;
        }

        private static ShapelessRecipe m_44292_(RegistryFriendlyByteBuf p_335962_) {
            String s = p_335962_.readUtf();
            CraftingBookCategory craftingbookcategory = p_335962_.readEnum(CraftingBookCategory.class);
            int i = p_335962_.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);
            nonnulllist.replaceAll(p_327214_ -> Ingredient.f_317040_.m_318688_(p_335962_));
            ItemStack itemstack = ItemStack.f_315801_.m_318688_(p_335962_);
            return new ShapelessRecipe(s, craftingbookcategory, itemstack, nonnulllist);
        }

        private static void m_44280_(RegistryFriendlyByteBuf p_329239_, ShapelessRecipe pRecipe) {
            p_329239_.writeUtf(pRecipe.group);
            p_329239_.writeEnum(pRecipe.category);
            p_329239_.writeVarInt(pRecipe.ingredients.size());

            for (Ingredient ingredient : pRecipe.ingredients) {
                Ingredient.f_317040_.m_318638_(p_329239_, ingredient);
            }

            ItemStack.f_315801_.m_318638_(p_329239_, pRecipe.result);
        }
    }
}

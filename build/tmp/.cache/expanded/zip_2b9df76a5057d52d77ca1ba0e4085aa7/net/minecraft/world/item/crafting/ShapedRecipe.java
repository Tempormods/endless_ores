package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ShapedRecipe implements CraftingRecipe, net.minecraftforge.common.crafting.IShapedRecipe<CraftingContainer> {
    static int MAX_WIDTH = 3;
    static int MAX_HEIGHT = 3;
    /**
     * Expand the max width and height allowed in the deserializer.
     * This should be called by modders who add custom crafting tables that are larger than the vanilla 3x3.
     * @param width your max recipe width
     * @param height your max recipe height
     */
    public static void setCraftingSize(int width, int height) {
        if (MAX_WIDTH < width) MAX_WIDTH = width;
        if (MAX_HEIGHT < height) MAX_HEIGHT = height;
    }
    final ShapedRecipePattern f_302516_;
    final ItemStack result;
    final String group;
    final CraftingBookCategory category;
    final boolean showNotification;

    public ShapedRecipe(String pGroup, CraftingBookCategory pCategory, ShapedRecipePattern p_312200_, ItemStack pResult, boolean p_310619_) {
        this.group = pGroup;
        this.category = pCategory;
        this.f_302516_ = p_312200_;
        this.result = pResult;
        this.showNotification = p_310619_;
    }

    public ShapedRecipe(String pGroup, CraftingBookCategory pCategory, ShapedRecipePattern p_310709_, ItemStack pResult) {
        this(pGroup, pCategory, p_310709_, pResult, true);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHAPED_RECIPE;
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
    public ItemStack getResultItem(HolderLookup.Provider p_332111_) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.f_302516_.f_303265_();
    }

    @Override
    public boolean showNotification() {
        return this.showNotification;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth >= this.f_302516_.f_303446_() && pHeight >= this.f_302516_.f_302375_();
    }

    public boolean matches(CraftingContainer pInv, Level pLevel) {
        return this.f_302516_.m_304908_(pInv);
    }

    public ItemStack assemble(CraftingContainer pContainer, HolderLookup.Provider p_333236_) {
        return this.getResultItem(p_333236_).copy();
    }

    public int getWidth() {
        return this.f_302516_.f_303446_();
    }

    public int getHeight() {
        return this.f_302516_.f_302375_();
    }

    @Override
    public int getRecipeWidth() {
        return getWidth();
    }

    @Override
    public int getRecipeHeight() {
        return getHeight();
    }

    @Override
    public boolean isIncomplete() {
        NonNullList<Ingredient> nonnulllist = this.getIngredients();
        return nonnulllist.isEmpty() || nonnulllist.stream().filter(p_151277_ -> !p_151277_.isEmpty()).anyMatch(net.minecraftforge.common.ForgeHooks::hasNoElements);
    }

    public static class Serializer implements RecipeSerializer<ShapedRecipe> {
        public static final MapCodec<ShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(
            p_327208_ -> p_327208_.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(p_309251_ -> p_309251_.group),
                        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(p_309253_ -> p_309253_.category),
                        ShapedRecipePattern.f_302908_.forGetter(p_309254_ -> p_309254_.f_302516_),
                        ItemStack.f_315780_.fieldOf("result").forGetter(p_309252_ -> p_309252_.result),
                        Codec.BOOL.optionalFieldOf("show_notification", Boolean.valueOf(true)).forGetter(p_309255_ -> p_309255_.showNotification)
                    )
                    .apply(p_327208_, ShapedRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, ShapedRecipe> f_315001_ = StreamCodec.m_320617_(
            ShapedRecipe.Serializer::m_44223_, ShapedRecipe.Serializer::m_44238_
        );

        @Override
        public MapCodec<ShapedRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShapedRecipe> m_318841_() {
            return f_315001_;
        }

        private static ShapedRecipe m_44238_(RegistryFriendlyByteBuf p_335571_) {
            String s = p_335571_.readUtf();
            CraftingBookCategory craftingbookcategory = p_335571_.readEnum(CraftingBookCategory.class);
            ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.f_315058_.m_318688_(p_335571_);
            ItemStack itemstack = ItemStack.f_315801_.m_318688_(p_335571_);
            boolean flag = p_335571_.readBoolean();
            return new ShapedRecipe(s, craftingbookcategory, shapedrecipepattern, itemstack, flag);
        }

        private static void m_44223_(RegistryFriendlyByteBuf p_336365_, ShapedRecipe p_330934_) {
            p_336365_.writeUtf(p_330934_.group);
            p_336365_.writeEnum(p_330934_.category);
            ShapedRecipePattern.f_315058_.m_318638_(p_336365_, p_330934_.f_302516_);
            ItemStack.f_315801_.m_318638_(p_336365_, p_330934_.result);
            p_336365_.writeBoolean(p_330934_.showNotification);
        }
    }
}

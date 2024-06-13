package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public abstract class SingleItemRecipe implements Recipe<Container> {
    protected final Ingredient ingredient;
    protected final ItemStack result;
    private final RecipeType<?> type;
    private final RecipeSerializer<?> serializer;
    protected final String group;

    public SingleItemRecipe(RecipeType<?> pType, RecipeSerializer<?> pSerializer, String pGroup, Ingredient pIngredient, ItemStack pResult) {
        this.type = pType;
        this.serializer = pSerializer;
        this.group = pGroup;
        this.ingredient = pIngredient;
        this.result = pResult;
    }

    @Override
    public RecipeType<?> getType() {
        return this.type;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.serializer;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider p_329765_) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.ingredient);
        return nonnulllist;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack assemble(Container pContainer, HolderLookup.Provider p_330907_) {
        return this.result.copy();
    }

    public interface Factory<T extends SingleItemRecipe> {
        T m_306940_(String p_310227_, Ingredient p_313029_, ItemStack p_312409_);
    }

    public static class Serializer<T extends SingleItemRecipe> implements RecipeSerializer<T> {
        final SingleItemRecipe.Factory<T> factory;
        private final MapCodec<T> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, T> f_314479_;

        protected Serializer(SingleItemRecipe.Factory<T> p_311205_) {
            this.factory = p_311205_;
            this.codec = RecordCodecBuilder.mapCodec(
                p_327217_ -> p_327217_.group(
                            Codec.STRING.optionalFieldOf("group", "").forGetter(p_298324_ -> p_298324_.group),
                            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(p_299566_ -> p_299566_.ingredient),
                            ItemStack.f_315780_.fieldOf("result").forGetter(p_301692_ -> p_301692_.result)
                        )
                        .apply(p_327217_, p_311205_::m_306940_)
            );
            this.f_314479_ = StreamCodec.m_321516_(
                ByteBufCodecs.f_315450_,
                p_327219_ -> p_327219_.group,
                Ingredient.f_317040_,
                p_327218_ -> p_327218_.ingredient,
                ItemStack.f_315801_,
                p_327215_ -> p_327215_.result,
                p_311205_::m_306940_
            );
        }

        @Override
        public MapCodec<T> codec() {
            return this.codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> m_318841_() {
            return this.f_314479_;
        }
    }
}
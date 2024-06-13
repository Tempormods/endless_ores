package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class SimpleCookingSerializer<T extends AbstractCookingRecipe> implements RecipeSerializer<T> {
    private final AbstractCookingRecipe.Factory<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> f_315376_;

    public SimpleCookingSerializer(AbstractCookingRecipe.Factory<T> p_310761_, int pDefaultCookingTime) {
        this.factory = p_310761_;
        this.codec = RecordCodecBuilder.mapCodec(
            p_296927_ -> p_296927_.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(p_296921_ -> p_296921_.group),
                        CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter(p_296924_ -> p_296924_.category),
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(p_296920_ -> p_296920_.ingredient),
                        ItemStack.f_316270_.fieldOf("result").forGetter(p_296923_ -> p_296923_.result),
                        Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(p_296922_ -> p_296922_.experience),
                        Codec.INT.fieldOf("cookingtime").orElse(pDefaultCookingTime).forGetter(p_296919_ -> p_296919_.cookingTime)
                    )
                    .apply(p_296927_, p_310761_::m_306886_)
        );
        this.f_315376_ = StreamCodec.m_320617_(this::m_44337_, this::m_44343_);
    }

    @Override
    public MapCodec<T> codec() {
        return this.codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> m_318841_() {
        return this.f_315376_;
    }

    private T m_44343_(RegistryFriendlyByteBuf p_336000_) {
        String s = p_336000_.readUtf();
        CookingBookCategory cookingbookcategory = p_336000_.readEnum(CookingBookCategory.class);
        Ingredient ingredient = Ingredient.f_317040_.m_318688_(p_336000_);
        ItemStack itemstack = ItemStack.f_315801_.m_318688_(p_336000_);
        float f = p_336000_.readFloat();
        int i = p_336000_.readVarInt();
        return this.factory.m_306886_(s, cookingbookcategory, ingredient, itemstack, f, i);
    }

    private void m_44337_(RegistryFriendlyByteBuf p_334532_, T p_333006_) {
        p_334532_.writeUtf(p_333006_.group);
        p_334532_.writeEnum(p_333006_.category());
        Ingredient.f_317040_.m_318638_(p_334532_, p_333006_.ingredient);
        ItemStack.f_315801_.m_318638_(p_334532_, p_333006_.result);
        p_334532_.writeFloat(p_333006_.experience);
        p_334532_.writeVarInt(p_333006_.cookingTime);
    }

    public AbstractCookingRecipe m_306443_(
        String p_312339_, CookingBookCategory p_309654_, Ingredient p_310924_, ItemStack p_311755_, float p_311505_, int p_311630_
    ) {
        return this.factory.m_306886_(p_312339_, p_309654_, p_310924_, p_311755_, p_311505_, p_311630_);
    }
}
package net.minecraft.world.item.trading;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record ItemCost(Holder<Item> f_316448_, int f_314096_, DataComponentPredicate f_317105_, ItemStack f_315785_) {
    public static final Codec<ItemCost> f_314864_ = RecordCodecBuilder.create(
        p_328053_ -> p_328053_.group(
                    ItemStack.f_303113_.fieldOf("id").forGetter(ItemCost::f_316448_),
                    ExtraCodecs.POSITIVE_INT.fieldOf("count").orElse(1).forGetter(ItemCost::f_314096_),
                    DataComponentPredicate.f_314199_.optionalFieldOf("components", DataComponentPredicate.f_314891_).forGetter(ItemCost::f_317105_)
                )
                .apply(p_328053_, ItemCost::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemCost> f_317082_ = StreamCodec.m_321516_(
        ByteBufCodecs.m_322636_(Registries.ITEM),
        ItemCost::f_316448_,
        ByteBufCodecs.f_316730_,
        ItemCost::f_314096_,
        DataComponentPredicate.f_317058_,
        ItemCost::f_317105_,
        ItemCost::new
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<ItemCost>> f_316200_ = f_317082_.m_321801_(ByteBufCodecs::m_319027_);

    public ItemCost(ItemLike p_333321_) {
        this(p_333321_, 1);
    }

    public ItemCost(ItemLike p_332783_, int p_331715_) {
        this(p_332783_.asItem().builtInRegistryHolder(), p_331715_, DataComponentPredicate.f_314891_);
    }

    public ItemCost(Holder<Item> p_331233_, int p_334492_, DataComponentPredicate p_330788_) {
        this(p_331233_, p_334492_, p_330788_, m_324643_(p_331233_, p_334492_, p_330788_));
    }

    public ItemCost m_322130_(UnaryOperator<DataComponentPredicate.Builder> p_328625_) {
        return new ItemCost(this.f_316448_, this.f_314096_, p_328625_.apply(DataComponentPredicate.m_321115_()).m_324461_());
    }

    private static ItemStack m_324643_(Holder<Item> p_329043_, int p_329370_, DataComponentPredicate p_330789_) {
        return new ItemStack(p_329043_, p_329370_, p_330789_.m_323520_());
    }

    public boolean m_320083_(ItemStack p_331178_) {
        return p_331178_.is(this.f_316448_) && this.f_317105_.m_323113_(p_331178_);
    }
}
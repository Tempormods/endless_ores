package net.minecraft.world.item.component;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

public record ItemLore(List<Component> f_316209_, List<Component> f_317111_) implements TooltipProvider {
    public static final ItemLore f_315439_ = new ItemLore(List.of());
    public static final int f_314243_ = 256;
    private static final Style f_316791_ = Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(true);
    public static final Codec<ItemLore> f_316205_ = ComponentSerialization.f_303675_.sizeLimitedListOf(256).xmap(ItemLore::new, ItemLore::f_316209_);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemLore> f_316332_ = ComponentSerialization.f_315335_
        .m_321801_(ByteBufCodecs.m_319259_(256))
        .m_323038_(ItemLore::new, ItemLore::f_316209_);

    public ItemLore(List<Component> p_330285_) {
        this(p_330285_, Lists.transform(p_330285_, p_329096_ -> ComponentUtils.mergeStyles(p_329096_.copy(), f_316791_)));
    }

    public ItemLore(List<Component> f_316209_, List<Component> f_317111_) {
        if (f_316209_.size() > 256) {
            throw new IllegalArgumentException("Got " + f_316209_.size() + " lines, but maximum is 256");
        } else {
            this.f_316209_ = f_316209_;
            this.f_317111_ = f_317111_;
        }
    }

    public ItemLore m_320019_(Component p_328621_) {
        return new ItemLore(Util.m_324319_(this.f_316209_, p_328621_));
    }

    @Override
    public void m_319025_(Item.TooltipContext p_329761_, Consumer<Component> p_332607_, TooltipFlag p_328590_) {
        this.f_317111_.forEach(p_332607_);
    }
}
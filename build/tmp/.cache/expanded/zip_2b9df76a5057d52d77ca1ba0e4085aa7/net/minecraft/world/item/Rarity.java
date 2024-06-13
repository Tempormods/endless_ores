package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum Rarity implements StringRepresentable {
    COMMON(0, "common", ChatFormatting.WHITE),
    UNCOMMON(1, "uncommon", ChatFormatting.YELLOW),
    RARE(2, "rare", ChatFormatting.AQUA),
    EPIC(3, "epic", ChatFormatting.LIGHT_PURPLE);

    public static final Codec<Rarity> f_317080_ = StringRepresentable.m_306774_(Rarity::values);
    public static final IntFunction<Rarity> f_315799_ = ByIdMap.continuous(p_328775_ -> p_328775_.f_316805_, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, Rarity> f_316344_ = ByteBufCodecs.m_321301_(f_315799_, p_330010_ -> p_330010_.f_316805_);
    private final int f_316805_;
    private final String f_314266_;
    private final ChatFormatting color;

    private Rarity(final int p_330136_, final String p_327766_, final ChatFormatting pColor) {
        this.f_316805_ = p_330136_;
        this.f_314266_ = p_327766_;
        this.color = pColor;
    }

    public ChatFormatting m_321696_() {
        return this.color;
    }

    @Override
    public String getSerializedName() {
        return this.f_314266_;
    }
}
package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum CraftingBookCategory implements StringRepresentable {
    BUILDING("building", 0),
    REDSTONE("redstone", 1),
    EQUIPMENT("equipment", 2),
    MISC("misc", 3);

    public static final Codec<CraftingBookCategory> CODEC = StringRepresentable.fromEnum(CraftingBookCategory::values);
    public static final IntFunction<CraftingBookCategory> f_316439_ = ByIdMap.continuous(
        CraftingBookCategory::m_320418_, values(), ByIdMap.OutOfBoundsStrategy.ZERO
    );
    public static final StreamCodec<ByteBuf, CraftingBookCategory> f_315540_ = ByteBufCodecs.m_321301_(f_316439_, CraftingBookCategory::m_320418_);
    private final String name;
    private final int f_314845_;

    private CraftingBookCategory(final String pName, final int p_331077_) {
        this.name = pName;
        this.f_314845_ = p_331077_;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private int m_320418_() {
        return this.f_314845_;
    }
}
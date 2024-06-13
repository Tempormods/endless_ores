package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CustomModelData(int f_314084_) {
    public static final CustomModelData f_316694_ = new CustomModelData(0);
    public static final Codec<CustomModelData> f_315539_ = Codec.INT.xmap(CustomModelData::new, CustomModelData::f_314084_);
    public static final StreamCodec<ByteBuf, CustomModelData> f_316249_ = ByteBufCodecs.f_316730_.m_323038_(CustomModelData::new, CustomModelData::f_314084_);
}
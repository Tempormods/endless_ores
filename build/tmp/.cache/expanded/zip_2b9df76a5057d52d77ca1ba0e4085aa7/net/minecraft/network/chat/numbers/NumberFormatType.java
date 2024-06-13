package net.minecraft.network.chat.numbers;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface NumberFormatType<T extends NumberFormat> {
    MapCodec<T> m_305346_();

    StreamCodec<RegistryFriendlyByteBuf, T> m_321453_();
}
package net.minecraft.network.protocol;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;

public interface Packet<T extends PacketListener> {
    PacketType<? extends Packet<T>> write();

    void handle(T pHandler);

    default boolean isSkippable() {
        return false;
    }

    default boolean m_319635_() {
        return false;
    }

    static <B extends ByteBuf, T extends Packet<?>> StreamCodec<B, T> m_319422_(StreamMemberEncoder<B, T> p_334100_, StreamDecoder<B, T> p_335492_) {
        return StreamCodec.m_324771_(p_334100_, p_335492_);
    }
}
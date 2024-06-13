package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

public interface ProtocolInfo<T extends PacketListener> {
    ConnectionProtocol m_320326_();

    PacketFlow m_319133_();

    StreamCodec<ByteBuf, Packet<? super T>> m_319098_();

    @Nullable
    BundlerInfo m_320896_();

    public interface Unbound<T extends PacketListener, B extends ByteBuf> {
        ProtocolInfo<T> m_324476_(Function<ByteBuf, B> p_327995_);
    }
}
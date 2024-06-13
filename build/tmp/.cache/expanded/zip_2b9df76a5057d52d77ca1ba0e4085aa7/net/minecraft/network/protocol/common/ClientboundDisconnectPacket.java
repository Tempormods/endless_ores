package net.minecraft.network.protocol.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundDisconnectPacket(Component reason) implements Packet<ClientCommonPacketListener> {
    public static final StreamCodec<ByteBuf, ClientboundDisconnectPacket> f_315680_ = ComponentSerialization.f_314039_
        .m_323038_(ClientboundDisconnectPacket::new, ClientboundDisconnectPacket::reason);

    @Override
    public PacketType<ClientboundDisconnectPacket> write() {
        return CommonPacketTypes.f_314152_;
    }

    public void handle(ClientCommonPacketListener pHandler) {
        pHandler.handleDisconnect(this);
    }
}
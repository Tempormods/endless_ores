package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundServerDataPacket(Component motd, Optional<byte[]> iconBytes) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<ByteBuf, ClientboundServerDataPacket> f_316752_ = StreamCodec.m_320349_(
        ComponentSerialization.f_314039_,
        ClientboundServerDataPacket::motd,
        ByteBufCodecs.f_315847_.m_321801_(ByteBufCodecs::m_319027_),
        ClientboundServerDataPacket::iconBytes,
        ClientboundServerDataPacket::new
    );

    @Override
    public PacketType<ClientboundServerDataPacket> write() {
        return GamePacketTypes.f_314766_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleServerData(this);
    }
}
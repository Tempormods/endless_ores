package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundTabListPacket(Component header, Component footer) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTabListPacket> f_314256_ = StreamCodec.m_320349_(
        ComponentSerialization.f_316335_,
        ClientboundTabListPacket::header,
        ComponentSerialization.f_316335_,
        ClientboundTabListPacket::footer,
        ClientboundTabListPacket::new
    );

    @Override
    public PacketType<ClientboundTabListPacket> write() {
        return GamePacketTypes.f_313977_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleTabListCustomisation(this);
    }
}
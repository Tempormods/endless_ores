package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundSetSubtitleTextPacket(Component text) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetSubtitleTextPacket> f_315951_ = StreamCodec.m_322204_(
        ComponentSerialization.f_316335_, ClientboundSetSubtitleTextPacket::text, ClientboundSetSubtitleTextPacket::new
    );

    @Override
    public PacketType<ClientboundSetSubtitleTextPacket> write() {
        return GamePacketTypes.f_315692_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.setSubtitleText(this);
    }
}
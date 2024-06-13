package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundSetTitleTextPacket(Component text) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetTitleTextPacket> f_317155_ = StreamCodec.m_322204_(
        ComponentSerialization.f_316335_, ClientboundSetTitleTextPacket::text, ClientboundSetTitleTextPacket::new
    );

    @Override
    public PacketType<ClientboundSetTitleTextPacket> write() {
        return GamePacketTypes.f_315546_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.setTitleText(this);
    }
}
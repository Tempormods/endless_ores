package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundSetActionBarTextPacket(Component text) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetActionBarTextPacket> f_317133_ = StreamCodec.m_322204_(
        ComponentSerialization.f_316335_, ClientboundSetActionBarTextPacket::text, ClientboundSetActionBarTextPacket::new
    );

    @Override
    public PacketType<ClientboundSetActionBarTextPacket> write() {
        return GamePacketTypes.f_315824_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.setActionBarText(this);
    }
}
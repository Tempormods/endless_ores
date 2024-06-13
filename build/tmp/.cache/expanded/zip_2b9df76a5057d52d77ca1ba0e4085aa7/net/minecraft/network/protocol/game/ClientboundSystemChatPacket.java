package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundSystemChatPacket(Component content, boolean overlay) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSystemChatPacket> f_314993_ = StreamCodec.m_320349_(
        ComponentSerialization.f_316335_,
        ClientboundSystemChatPacket::content,
        ByteBufCodecs.f_315514_,
        ClientboundSystemChatPacket::overlay,
        ClientboundSystemChatPacket::new
    );

    @Override
    public PacketType<ClientboundSystemChatPacket> write() {
        return GamePacketTypes.f_317115_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleSystemChat(this);
    }

    @Override
    public boolean isSkippable() {
        return true;
    }
}
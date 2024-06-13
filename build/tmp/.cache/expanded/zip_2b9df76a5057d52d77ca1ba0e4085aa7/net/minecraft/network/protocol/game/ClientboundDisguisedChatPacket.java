package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundDisguisedChatPacket(Component message, ChatType.Bound chatType) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDisguisedChatPacket> f_314761_ = StreamCodec.m_320349_(
        ComponentSerialization.f_316335_,
        ClientboundDisguisedChatPacket::message,
        ChatType.Bound.f_316893_,
        ClientboundDisguisedChatPacket::chatType,
        ClientboundDisguisedChatPacket::new
    );

    @Override
    public PacketType<ClientboundDisguisedChatPacket> write() {
        return GamePacketTypes.f_316364_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleDisguisedChat(this);
    }

    @Override
    public boolean isSkippable() {
        return true;
    }
}
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundDeleteChatPacket(MessageSignature.Packed messageSignature) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundDeleteChatPacket> f_315431_ = Packet.m_319422_(
        ClientboundDeleteChatPacket::m_241025_, ClientboundDeleteChatPacket::new
    );

    private ClientboundDeleteChatPacket(FriendlyByteBuf pBuffer) {
        this(MessageSignature.Packed.read(pBuffer));
    }

    private void m_241025_(FriendlyByteBuf pBuffer) {
        MessageSignature.Packed.write(pBuffer, this.messageSignature);
    }

    @Override
    public PacketType<ClientboundDeleteChatPacket> write() {
        return GamePacketTypes.f_315753_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleDeleteChat(this);
    }
}
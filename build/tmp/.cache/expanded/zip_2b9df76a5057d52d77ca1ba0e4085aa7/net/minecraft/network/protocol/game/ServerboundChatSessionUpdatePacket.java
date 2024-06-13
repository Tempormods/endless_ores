package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundChatSessionUpdatePacket(RemoteChatSession.Data chatSession) implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundChatSessionUpdatePacket> f_314546_ = Packet.m_319422_(
        ServerboundChatSessionUpdatePacket::m_252740_, ServerboundChatSessionUpdatePacket::new
    );

    private ServerboundChatSessionUpdatePacket(FriendlyByteBuf pBuffer) {
        this(RemoteChatSession.Data.read(pBuffer));
    }

    private void m_252740_(FriendlyByteBuf pBuffer) {
        RemoteChatSession.Data.write(pBuffer, this.chatSession);
    }

    @Override
    public PacketType<ServerboundChatSessionUpdatePacket> write() {
        return GamePacketTypes.f_315111_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleChatSessionUpdate(this);
    }
}
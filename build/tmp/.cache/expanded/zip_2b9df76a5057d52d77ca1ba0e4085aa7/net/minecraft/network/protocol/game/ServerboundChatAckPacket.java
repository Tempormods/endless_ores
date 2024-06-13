package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundChatAckPacket(int offset) implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundChatAckPacket> f_314055_ = Packet.m_319422_(
        ServerboundChatAckPacket::m_242013_, ServerboundChatAckPacket::new
    );

    private ServerboundChatAckPacket(FriendlyByteBuf pBuffer) {
        this(pBuffer.readVarInt());
    }

    private void m_242013_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.offset);
    }

    @Override
    public PacketType<ServerboundChatAckPacket> write() {
        return GamePacketTypes.f_316322_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleChatAck(this);
    }
}
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundBlockChangedAckPacket(int sequence) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundBlockChangedAckPacket> f_314302_ = Packet.m_319422_(
        ClientboundBlockChangedAckPacket::m_237583_, ClientboundBlockChangedAckPacket::new
    );

    private ClientboundBlockChangedAckPacket(FriendlyByteBuf pBuffer) {
        this(pBuffer.readVarInt());
    }

    private void m_237583_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.sequence);
    }

    @Override
    public PacketType<ClientboundBlockChangedAckPacket> write() {
        return GamePacketTypes.f_315632_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleBlockChangedAck(this);
    }
}
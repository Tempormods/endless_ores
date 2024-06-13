package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundChunkBatchFinishedPacket(int batchSize) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundChunkBatchFinishedPacket> f_315055_ = Packet.m_319422_(
        ClientboundChunkBatchFinishedPacket::m_294868_, ClientboundChunkBatchFinishedPacket::new
    );

    private ClientboundChunkBatchFinishedPacket(FriendlyByteBuf pBuffer) {
        this(pBuffer.readVarInt());
    }

    private void m_294868_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.batchSize);
    }

    @Override
    public PacketType<ClientboundChunkBatchFinishedPacket> write() {
        return GamePacketTypes.f_316747_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleChunkBatchFinished(this);
    }
}
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundChunkBatchReceivedPacket(float desiredChunksPerTick) implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundChunkBatchReceivedPacket> f_314590_ = Packet.m_319422_(
        ServerboundChunkBatchReceivedPacket::m_294366_, ServerboundChunkBatchReceivedPacket::new
    );

    private ServerboundChunkBatchReceivedPacket(FriendlyByteBuf pBuffer) {
        this(pBuffer.readFloat());
    }

    private void m_294366_(FriendlyByteBuf pBuffer) {
        pBuffer.writeFloat(this.desiredChunksPerTick);
    }

    @Override
    public PacketType<ServerboundChunkBatchReceivedPacket> write() {
        return GamePacketTypes.f_315939_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleChunkBatchReceived(this);
    }
}
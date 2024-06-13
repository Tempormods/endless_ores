package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.ChunkPos;

public record ClientboundForgetLevelChunkPacket(ChunkPos pos) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundForgetLevelChunkPacket> f_315427_ = Packet.m_319422_(
        ClientboundForgetLevelChunkPacket::m_132150_, ClientboundForgetLevelChunkPacket::new
    );

    private ClientboundForgetLevelChunkPacket(FriendlyByteBuf pBuffer) {
        this(pBuffer.readChunkPos());
    }

    private void m_132150_(FriendlyByteBuf pBuffer) {
        pBuffer.writeChunkPos(this.pos);
    }

    @Override
    public PacketType<ClientboundForgetLevelChunkPacket> write() {
        return GamePacketTypes.f_314033_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleForgetLevelChunk(this);
    }
}
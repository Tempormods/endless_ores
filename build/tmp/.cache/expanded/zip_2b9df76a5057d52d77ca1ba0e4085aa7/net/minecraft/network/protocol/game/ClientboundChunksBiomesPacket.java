package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

public record ClientboundChunksBiomesPacket(List<ClientboundChunksBiomesPacket.ChunkBiomeData> chunkBiomeData) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundChunksBiomesPacket> f_313994_ = Packet.m_319422_(
        ClientboundChunksBiomesPacket::m_274464_, ClientboundChunksBiomesPacket::new
    );
    private static final int TWO_MEGABYTES = 2097152;

    private ClientboundChunksBiomesPacket(FriendlyByteBuf pBuffer) {
        this(pBuffer.readList(ClientboundChunksBiomesPacket.ChunkBiomeData::new));
    }

    public static ClientboundChunksBiomesPacket forChunks(List<LevelChunk> pChunks) {
        return new ClientboundChunksBiomesPacket(pChunks.stream().map(ClientboundChunksBiomesPacket.ChunkBiomeData::new).toList());
    }

    private void m_274464_(FriendlyByteBuf pBuffer) {
        pBuffer.writeCollection(this.chunkBiomeData, (p_275199_, p_275200_) -> p_275200_.write(p_275199_));
    }

    @Override
    public PacketType<ClientboundChunksBiomesPacket> write() {
        return GamePacketTypes.f_316412_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleChunksBiomes(this);
    }

    public static record ChunkBiomeData(ChunkPos pos, byte[] buffer) {
        public ChunkBiomeData(LevelChunk pChunk) {
            this(pChunk.getPos(), new byte[calculateChunkSize(pChunk)]);
            extractChunkData(new FriendlyByteBuf(this.getWriteBuffer()), pChunk);
        }

        public ChunkBiomeData(FriendlyByteBuf pBuffer) {
            this(pBuffer.readChunkPos(), pBuffer.readByteArray(2097152));
        }

        private static int calculateChunkSize(LevelChunk pChunk) {
            int i = 0;

            for (LevelChunkSection levelchunksection : pChunk.getSections()) {
                i += levelchunksection.getBiomes().getSerializedSize();
            }

            return i;
        }

        public FriendlyByteBuf getReadBuffer() {
            return new FriendlyByteBuf(Unpooled.wrappedBuffer(this.buffer));
        }

        private ByteBuf getWriteBuffer() {
            ByteBuf bytebuf = Unpooled.wrappedBuffer(this.buffer);
            bytebuf.writerIndex(0);
            return bytebuf;
        }

        public static void extractChunkData(FriendlyByteBuf pBuffer, LevelChunk pChunk) {
            for (LevelChunkSection levelchunksection : pChunk.getSections()) {
                levelchunksection.getBiomes().write(pBuffer);
            }
        }

        public void write(FriendlyByteBuf pBuffer) {
            pBuffer.writeChunkPos(this.pos);
            pBuffer.writeByteArray(this.buffer);
        }
    }
}
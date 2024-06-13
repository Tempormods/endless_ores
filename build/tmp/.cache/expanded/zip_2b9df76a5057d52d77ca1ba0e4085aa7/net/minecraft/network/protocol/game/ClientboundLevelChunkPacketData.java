package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;

public class ClientboundLevelChunkPacketData {
    private static final int TWO_MEGABYTES = 2097152;
    private final CompoundTag heightmaps;
    private final byte[] buffer;
    private final List<ClientboundLevelChunkPacketData.BlockEntityInfo> blockEntitiesData;

    public ClientboundLevelChunkPacketData(LevelChunk pLevelChunk) {
        this.heightmaps = new CompoundTag();

        for (Entry<Heightmap.Types, Heightmap> entry : pLevelChunk.getHeightmaps()) {
            if (entry.getKey().sendToClient()) {
                this.heightmaps.put(entry.getKey().getSerializationKey(), new LongArrayTag(entry.getValue().getRawData()));
            }
        }

        this.buffer = new byte[calculateChunkSize(pLevelChunk)];
        extractChunkData(new FriendlyByteBuf(this.getWriteBuffer()), pLevelChunk);
        this.blockEntitiesData = Lists.newArrayList();

        for (Entry<BlockPos, BlockEntity> entry1 : pLevelChunk.getBlockEntities().entrySet()) {
            this.blockEntitiesData.add(ClientboundLevelChunkPacketData.BlockEntityInfo.create(entry1.getValue()));
        }
    }

    public ClientboundLevelChunkPacketData(RegistryFriendlyByteBuf p_335775_, int pChunkX, int pChunkZ) {
        this.heightmaps = p_335775_.readNbt();
        if (this.heightmaps == null) {
            throw new RuntimeException("Can't read heightmap in packet for [" + pChunkX + ", " + pChunkZ + "]");
        } else {
            int i = p_335775_.readVarInt();
            if (i > 2097152) {
                throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
            } else {
                this.buffer = new byte[i];
                p_335775_.readBytes(this.buffer);
                this.blockEntitiesData = ClientboundLevelChunkPacketData.BlockEntityInfo.f_315693_.m_318688_(p_335775_);
            }
        }
    }

    public void write(RegistryFriendlyByteBuf p_331012_) {
        p_331012_.writeNbt(this.heightmaps);
        p_331012_.writeVarInt(this.buffer.length);
        p_331012_.writeBytes(this.buffer);
        ClientboundLevelChunkPacketData.BlockEntityInfo.f_315693_.m_318638_(p_331012_, this.blockEntitiesData);
    }

    private static int calculateChunkSize(LevelChunk pChunk) {
        int i = 0;

        for (LevelChunkSection levelchunksection : pChunk.getSections()) {
            i += levelchunksection.getSerializedSize();
        }

        return i;
    }

    private ByteBuf getWriteBuffer() {
        ByteBuf bytebuf = Unpooled.wrappedBuffer(this.buffer);
        bytebuf.writerIndex(0);
        return bytebuf;
    }

    public static void extractChunkData(FriendlyByteBuf pBuffer, LevelChunk pChunk) {
        for (LevelChunkSection levelchunksection : pChunk.getSections()) {
            levelchunksection.write(pBuffer);
        }
    }

    public Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> getBlockEntitiesTagsConsumer(int pChunkX, int pChunkZ) {
        return p_195663_ -> this.getBlockEntitiesTags(p_195663_, pChunkX, pChunkZ);
    }

    private void getBlockEntitiesTags(ClientboundLevelChunkPacketData.BlockEntityTagOutput pOutput, int pChunkX, int pChunkZ) {
        int i = 16 * pChunkX;
        int j = 16 * pChunkZ;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (ClientboundLevelChunkPacketData.BlockEntityInfo clientboundlevelchunkpacketdata$blockentityinfo : this.blockEntitiesData) {
            int k = i + SectionPos.sectionRelative(clientboundlevelchunkpacketdata$blockentityinfo.packedXZ >> 4);
            int l = j + SectionPos.sectionRelative(clientboundlevelchunkpacketdata$blockentityinfo.packedXZ);
            blockpos$mutableblockpos.set(k, clientboundlevelchunkpacketdata$blockentityinfo.y, l);
            pOutput.accept(
                blockpos$mutableblockpos, clientboundlevelchunkpacketdata$blockentityinfo.type, clientboundlevelchunkpacketdata$blockentityinfo.tag
            );
        }
    }

    public FriendlyByteBuf getReadBuffer() {
        return new FriendlyByteBuf(Unpooled.wrappedBuffer(this.buffer));
    }

    public CompoundTag getHeightmaps() {
        return this.heightmaps;
    }

    static class BlockEntityInfo {
        public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundLevelChunkPacketData.BlockEntityInfo> f_314633_ = StreamCodec.m_324771_(
            ClientboundLevelChunkPacketData.BlockEntityInfo::write, ClientboundLevelChunkPacketData.BlockEntityInfo::new
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, List<ClientboundLevelChunkPacketData.BlockEntityInfo>> f_315693_ = f_314633_.m_321801_(
            ByteBufCodecs.m_324765_()
        );
        final int packedXZ;
        final int y;
        final BlockEntityType<?> type;
        @Nullable
        final CompoundTag tag;

        private BlockEntityInfo(int pPackedXZ, int pY, BlockEntityType<?> pType, @Nullable CompoundTag pTag) {
            this.packedXZ = pPackedXZ;
            this.y = pY;
            this.type = pType;
            this.tag = pTag;
        }

        private BlockEntityInfo(RegistryFriendlyByteBuf p_335103_) {
            this.packedXZ = p_335103_.readByte();
            this.y = p_335103_.readShort();
            this.type = ByteBufCodecs.m_320159_(Registries.BLOCK_ENTITY_TYPE).m_318688_(p_335103_);
            this.tag = p_335103_.readNbt();
        }

        private void write(RegistryFriendlyByteBuf p_332659_) {
            p_332659_.writeByte(this.packedXZ);
            p_332659_.writeShort(this.y);
            ByteBufCodecs.m_320159_(Registries.BLOCK_ENTITY_TYPE).m_318638_(p_332659_, this.type);
            p_332659_.writeNbt(this.tag);
        }

        static ClientboundLevelChunkPacketData.BlockEntityInfo create(BlockEntity pBlockEntity) {
            CompoundTag compoundtag = pBlockEntity.getUpdateTag(pBlockEntity.getLevel().registryAccess());
            BlockPos blockpos = pBlockEntity.getBlockPos();
            int i = SectionPos.sectionRelative(blockpos.getX()) << 4 | SectionPos.sectionRelative(blockpos.getZ());
            return new ClientboundLevelChunkPacketData.BlockEntityInfo(
                i, blockpos.getY(), pBlockEntity.getType(), compoundtag.isEmpty() ? null : compoundtag
            );
        }
    }

    @FunctionalInterface
    public interface BlockEntityTagOutput {
        void accept(BlockPos pPos, BlockEntityType<?> pType, @Nullable CompoundTag pNbt);
    }
}
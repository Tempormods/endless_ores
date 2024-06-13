package net.minecraft.network.protocol.common.custom;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public record BrainDebugPayload(BrainDebugPayload.BrainDump brainDump) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, BrainDebugPayload> f_316808_ = CustomPacketPayload.m_320054_(
        BrainDebugPayload::m_294603_, BrainDebugPayload::new
    );
    public static final CustomPacketPayload.Type<BrainDebugPayload> f_316333_ = CustomPacketPayload.m_319865_("debug/brain");

    private BrainDebugPayload(FriendlyByteBuf pBuffer) {
        this(new BrainDebugPayload.BrainDump(pBuffer));
    }

    private void m_294603_(FriendlyByteBuf pBuffer) {
        this.brainDump.write(pBuffer);
    }

    @Override
    public CustomPacketPayload.Type<BrainDebugPayload> m_293297_() {
        return f_316333_;
    }

    public static record BrainDump(
        UUID uuid,
        int id,
        String name,
        String profession,
        int xp,
        float health,
        float maxHealth,
        Vec3 pos,
        String inventory,
        @Nullable Path path,
        boolean wantsGolem,
        int angerLevel,
        List<String> activities,
        List<String> behaviors,
        List<String> memories,
        List<String> gossips,
        Set<BlockPos> pois,
        Set<BlockPos> potentialPois
    ) {
        public BrainDump(FriendlyByteBuf pBuffer) {
            this(
                pBuffer.readUUID(),
                pBuffer.readInt(),
                pBuffer.readUtf(),
                pBuffer.readUtf(),
                pBuffer.readInt(),
                pBuffer.readFloat(),
                pBuffer.readFloat(),
                pBuffer.readVec3(),
                pBuffer.readUtf(),
                pBuffer.readNullable(Path::createFromStream),
                pBuffer.readBoolean(),
                pBuffer.readInt(),
                pBuffer.readList(FriendlyByteBuf::readUtf),
                pBuffer.readList(FriendlyByteBuf::readUtf),
                pBuffer.readList(FriendlyByteBuf::readUtf),
                pBuffer.readList(FriendlyByteBuf::readUtf),
                pBuffer.readCollection(HashSet::new, BlockPos.f_316462_),
                pBuffer.readCollection(HashSet::new, BlockPos.f_316462_)
            );
        }

        public void write(FriendlyByteBuf pBuffer) {
            pBuffer.writeUUID(this.uuid);
            pBuffer.writeInt(this.id);
            pBuffer.writeUtf(this.name);
            pBuffer.writeUtf(this.profession);
            pBuffer.writeInt(this.xp);
            pBuffer.writeFloat(this.health);
            pBuffer.writeFloat(this.maxHealth);
            pBuffer.writeVec3(this.pos);
            pBuffer.writeUtf(this.inventory);
            pBuffer.m_321806_(this.path, (p_297936_, p_301045_) -> p_301045_.writeToStream(p_297936_));
            pBuffer.writeBoolean(this.wantsGolem);
            pBuffer.writeInt(this.angerLevel);
            pBuffer.writeCollection(this.activities, FriendlyByteBuf::writeUtf);
            pBuffer.writeCollection(this.behaviors, FriendlyByteBuf::writeUtf);
            pBuffer.writeCollection(this.memories, FriendlyByteBuf::writeUtf);
            pBuffer.writeCollection(this.gossips, FriendlyByteBuf::writeUtf);
            pBuffer.writeCollection(this.pois, BlockPos.f_316462_);
            pBuffer.writeCollection(this.potentialPois, BlockPos.f_316462_);
        }

        public boolean hasPoi(BlockPos pPos) {
            return this.pois.contains(pPos);
        }

        public boolean hasPotentialPoi(BlockPos pPos) {
            return this.potentialPois.contains(pPos);
        }
    }
}
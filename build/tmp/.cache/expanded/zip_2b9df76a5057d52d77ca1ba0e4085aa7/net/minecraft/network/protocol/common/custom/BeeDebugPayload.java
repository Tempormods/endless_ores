package net.minecraft.network.protocol.common.custom;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public record BeeDebugPayload(BeeDebugPayload.BeeInfo beeInfo) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, BeeDebugPayload> f_315531_ = CustomPacketPayload.m_320054_(
        BeeDebugPayload::write, BeeDebugPayload::new
    );
    public static final CustomPacketPayload.Type<BeeDebugPayload> f_314812_ = CustomPacketPayload.m_319865_("debug/bee");

    private BeeDebugPayload(FriendlyByteBuf pBuffer) {
        this(new BeeDebugPayload.BeeInfo(pBuffer));
    }

    private void write(FriendlyByteBuf pBuffer) {
        this.beeInfo.write(pBuffer);
    }

    @Override
    public CustomPacketPayload.Type<BeeDebugPayload> m_293297_() {
        return f_314812_;
    }

    public static record BeeInfo(
        UUID uuid,
        int id,
        Vec3 pos,
        @Nullable Path path,
        @Nullable BlockPos hivePos,
        @Nullable BlockPos flowerPos,
        int travelTicks,
        Set<String> goals,
        List<BlockPos> blacklistedHives
    ) {
        public BeeInfo(FriendlyByteBuf pBuffer) {
            this(
                pBuffer.readUUID(),
                pBuffer.readInt(),
                pBuffer.readVec3(),
                pBuffer.readNullable(Path::createFromStream),
                pBuffer.readNullable(BlockPos.f_316462_),
                pBuffer.readNullable(BlockPos.f_316462_),
                pBuffer.readInt(),
                pBuffer.readCollection(HashSet::new, FriendlyByteBuf::readUtf),
                pBuffer.readList(BlockPos.f_316462_)
            );
        }

        public void write(FriendlyByteBuf pBuffer) {
            pBuffer.writeUUID(this.uuid);
            pBuffer.writeInt(this.id);
            pBuffer.writeVec3(this.pos);
            pBuffer.m_321806_(this.path, (p_297580_, p_297572_) -> p_297572_.writeToStream(p_297580_));
            pBuffer.m_321806_(this.hivePos, BlockPos.f_316462_);
            pBuffer.m_321806_(this.flowerPos, BlockPos.f_316462_);
            pBuffer.writeInt(this.travelTicks);
            pBuffer.writeCollection(this.goals, FriendlyByteBuf::writeUtf);
            pBuffer.writeCollection(this.blacklistedHives, BlockPos.f_316462_);
        }

        public boolean hasHive(BlockPos pPos) {
            return Objects.equals(pPos, this.hivePos);
        }

        public String generateName() {
            return DebugEntityNameGenerator.getEntityName(this.uuid);
        }

        @Override
        public String toString() {
            return this.generateName();
        }
    }
}
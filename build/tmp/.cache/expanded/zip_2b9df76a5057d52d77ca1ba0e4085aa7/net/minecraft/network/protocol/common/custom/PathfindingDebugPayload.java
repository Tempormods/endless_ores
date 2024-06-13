package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.pathfinder.Path;

public record PathfindingDebugPayload(int entityId, Path path, float maxNodeDistance) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, PathfindingDebugPayload> f_316062_ = CustomPacketPayload.m_320054_(
        PathfindingDebugPayload::m_294690_, PathfindingDebugPayload::new
    );
    public static final CustomPacketPayload.Type<PathfindingDebugPayload> f_313916_ = CustomPacketPayload.m_319865_("debug/path");

    private PathfindingDebugPayload(FriendlyByteBuf pBuffer) {
        this(pBuffer.readInt(), Path.createFromStream(pBuffer), pBuffer.readFloat());
    }

    private void m_294690_(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(this.entityId);
        this.path.writeToStream(pBuffer);
        pBuffer.writeFloat(this.maxNodeDistance);
    }

    @Override
    public CustomPacketPayload.Type<PathfindingDebugPayload> m_293297_() {
        return f_313916_;
    }
}
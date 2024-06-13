package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record NeighborUpdatesDebugPayload(long time, BlockPos pos) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, NeighborUpdatesDebugPayload> f_315097_ = CustomPacketPayload.m_320054_(
        NeighborUpdatesDebugPayload::m_293601_, NeighborUpdatesDebugPayload::new
    );
    public static final CustomPacketPayload.Type<NeighborUpdatesDebugPayload> f_316722_ = CustomPacketPayload.m_319865_("debug/neighbors_update");

    private NeighborUpdatesDebugPayload(FriendlyByteBuf pBuffer) {
        this(pBuffer.readVarLong(), pBuffer.readBlockPos());
    }

    private void m_293601_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarLong(this.time);
        pBuffer.writeBlockPos(this.pos);
    }

    @Override
    public CustomPacketPayload.Type<NeighborUpdatesDebugPayload> m_293297_() {
        return f_316722_;
    }
}
package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record PoiRemovedDebugPayload(BlockPos pos) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, PoiRemovedDebugPayload> f_315876_ = CustomPacketPayload.m_320054_(
        PoiRemovedDebugPayload::m_294344_, PoiRemovedDebugPayload::new
    );
    public static final CustomPacketPayload.Type<PoiRemovedDebugPayload> f_315180_ = CustomPacketPayload.m_319865_("debug/poi_removed");

    private PoiRemovedDebugPayload(FriendlyByteBuf pBuffer) {
        this(pBuffer.readBlockPos());
    }

    private void m_294344_(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(this.pos);
    }

    @Override
    public CustomPacketPayload.Type<PoiRemovedDebugPayload> m_293297_() {
        return f_315180_;
    }
}
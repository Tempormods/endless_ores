package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record PoiAddedDebugPayload(BlockPos pos, String f_315076_, int freeTicketCount) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, PoiAddedDebugPayload> f_315048_ = CustomPacketPayload.m_320054_(
        PoiAddedDebugPayload::m_293671_, PoiAddedDebugPayload::new
    );
    public static final CustomPacketPayload.Type<PoiAddedDebugPayload> f_315969_ = CustomPacketPayload.m_319865_("debug/poi_added");

    private PoiAddedDebugPayload(FriendlyByteBuf pBuffer) {
        this(pBuffer.readBlockPos(), pBuffer.readUtf(), pBuffer.readInt());
    }

    private void m_293671_(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(this.pos);
        pBuffer.writeUtf(this.f_315076_);
        pBuffer.writeInt(this.freeTicketCount);
    }

    @Override
    public CustomPacketPayload.Type<PoiAddedDebugPayload> m_293297_() {
        return f_315969_;
    }
}
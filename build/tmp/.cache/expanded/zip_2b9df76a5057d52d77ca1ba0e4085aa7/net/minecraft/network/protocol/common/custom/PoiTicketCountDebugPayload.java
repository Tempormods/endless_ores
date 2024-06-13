package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record PoiTicketCountDebugPayload(BlockPos pos, int freeTicketCount) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, PoiTicketCountDebugPayload> f_314785_ = CustomPacketPayload.m_320054_(
        PoiTicketCountDebugPayload::m_294509_, PoiTicketCountDebugPayload::new
    );
    public static final CustomPacketPayload.Type<PoiTicketCountDebugPayload> f_314448_ = CustomPacketPayload.m_319865_("debug/poi_ticket_count");

    private PoiTicketCountDebugPayload(FriendlyByteBuf pBuffer) {
        this(pBuffer.readBlockPos(), pBuffer.readInt());
    }

    private void m_294509_(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(this.pos);
        pBuffer.writeInt(this.freeTicketCount);
    }

    @Override
    public CustomPacketPayload.Type<PoiTicketCountDebugPayload> m_293297_() {
        return f_314448_;
    }
}
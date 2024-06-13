package net.minecraft.network.protocol.common.custom;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record RaidsDebugPayload(List<BlockPos> raidCenters) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, RaidsDebugPayload> f_314237_ = CustomPacketPayload.m_320054_(
        RaidsDebugPayload::m_293554_, RaidsDebugPayload::new
    );
    public static final CustomPacketPayload.Type<RaidsDebugPayload> f_314056_ = CustomPacketPayload.m_319865_("debug/raids");

    private RaidsDebugPayload(FriendlyByteBuf pBuffer) {
        this(pBuffer.readList(BlockPos.f_316462_));
    }

    private void m_293554_(FriendlyByteBuf pBuffer) {
        pBuffer.writeCollection(this.raidCenters, BlockPos.f_316462_);
    }

    @Override
    public CustomPacketPayload.Type<RaidsDebugPayload> m_293297_() {
        return f_314056_;
    }
}
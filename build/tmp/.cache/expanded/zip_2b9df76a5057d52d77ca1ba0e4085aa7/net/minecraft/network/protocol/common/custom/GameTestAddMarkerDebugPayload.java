package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record GameTestAddMarkerDebugPayload(BlockPos pos, int color, String text, int durationMs) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, GameTestAddMarkerDebugPayload> f_316392_ = CustomPacketPayload.m_320054_(
        GameTestAddMarkerDebugPayload::m_295394_, GameTestAddMarkerDebugPayload::new
    );
    public static final CustomPacketPayload.Type<GameTestAddMarkerDebugPayload> f_314261_ = CustomPacketPayload.m_319865_("debug/game_test_add_marker");

    private GameTestAddMarkerDebugPayload(FriendlyByteBuf pBuffer) {
        this(pBuffer.readBlockPos(), pBuffer.readInt(), pBuffer.readUtf(), pBuffer.readInt());
    }

    private void m_295394_(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(this.pos);
        pBuffer.writeInt(this.color);
        pBuffer.writeUtf(this.text);
        pBuffer.writeInt(this.durationMs);
    }

    @Override
    public CustomPacketPayload.Type<GameTestAddMarkerDebugPayload> m_293297_() {
        return f_314261_;
    }
}
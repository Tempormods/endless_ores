package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record GameTestClearMarkersDebugPayload() implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, GameTestClearMarkersDebugPayload> f_314722_ = CustomPacketPayload.m_320054_(
        GameTestClearMarkersDebugPayload::m_293341_, GameTestClearMarkersDebugPayload::new
    );
    public static final CustomPacketPayload.Type<GameTestClearMarkersDebugPayload> f_316988_ = CustomPacketPayload.m_319865_("debug/game_test_clear");

    private GameTestClearMarkersDebugPayload(FriendlyByteBuf pBuffer) {
        this();
    }

    private void m_293341_(FriendlyByteBuf pBuffer) {
    }

    @Override
    public CustomPacketPayload.Type<GameTestClearMarkersDebugPayload> m_293297_() {
        return f_316988_;
    }
}
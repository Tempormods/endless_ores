package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.gameevent.PositionSource;

public record GameEventListenerDebugPayload(PositionSource listenerPos, int listenerRange) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, GameEventListenerDebugPayload> f_316780_ = StreamCodec.m_320349_(
        PositionSource.f_315179_,
        GameEventListenerDebugPayload::listenerPos,
        ByteBufCodecs.f_316730_,
        GameEventListenerDebugPayload::listenerRange,
        GameEventListenerDebugPayload::new
    );
    public static final CustomPacketPayload.Type<GameEventListenerDebugPayload> f_315542_ = CustomPacketPayload.m_319865_("debug/game_event_listeners");

    @Override
    public CustomPacketPayload.Type<GameEventListenerDebugPayload> m_293297_() {
        return f_315542_;
    }
}
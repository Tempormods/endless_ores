package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public record GameEventDebugPayload(ResourceKey<GameEvent> f_314514_, Vec3 pos) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, GameEventDebugPayload> f_316925_ = CustomPacketPayload.m_320054_(
        GameEventDebugPayload::m_295273_, GameEventDebugPayload::new
    );
    public static final CustomPacketPayload.Type<GameEventDebugPayload> f_315465_ = CustomPacketPayload.m_319865_("debug/game_event");

    private GameEventDebugPayload(FriendlyByteBuf pBuffer) {
        this(pBuffer.readResourceKey(Registries.GAME_EVENT), pBuffer.readVec3());
    }

    private void m_295273_(FriendlyByteBuf pBuffer) {
        pBuffer.writeResourceKey(this.f_314514_);
        pBuffer.writeVec3(this.pos);
    }

    @Override
    public CustomPacketPayload.Type<GameEventDebugPayload> m_293297_() {
        return f_315465_;
    }
}
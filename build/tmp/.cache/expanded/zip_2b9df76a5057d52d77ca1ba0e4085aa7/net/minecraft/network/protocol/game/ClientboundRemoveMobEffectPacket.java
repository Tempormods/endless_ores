package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record ClientboundRemoveMobEffectPacket(int entityId, Holder<MobEffect> effect) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundRemoveMobEffectPacket> f_314015_ = StreamCodec.m_320349_(
        ByteBufCodecs.f_316730_,
        p_334154_ -> p_334154_.entityId,
        ByteBufCodecs.m_322636_(Registries.MOB_EFFECT),
        ClientboundRemoveMobEffectPacket::effect,
        ClientboundRemoveMobEffectPacket::new
    );

    @Override
    public PacketType<ClientboundRemoveMobEffectPacket> write() {
        return GamePacketTypes.f_316638_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleRemoveMobEffect(this);
    }

    @Nullable
    public Entity getEntity(Level pLevel) {
        return pLevel.getEntity(this.entityId);
    }
}
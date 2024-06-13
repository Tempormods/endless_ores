package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundPlayerCombatKillPacket(int playerId, Component message) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPlayerCombatKillPacket> f_316921_ = StreamCodec.m_320349_(
        ByteBufCodecs.f_316730_,
        ClientboundPlayerCombatKillPacket::playerId,
        ComponentSerialization.f_316335_,
        ClientboundPlayerCombatKillPacket::message,
        ClientboundPlayerCombatKillPacket::new
    );

    @Override
    public PacketType<ClientboundPlayerCombatKillPacket> write() {
        return GamePacketTypes.f_316354_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handlePlayerCombatKill(this);
    }

    @Override
    public boolean isSkippable() {
        return true;
    }
}
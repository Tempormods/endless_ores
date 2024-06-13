package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.damagesource.CombatTracker;

public class ClientboundPlayerCombatEndPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundPlayerCombatEndPacket> f_315956_ = Packet.m_319422_(
        ClientboundPlayerCombatEndPacket::m_179043_, ClientboundPlayerCombatEndPacket::new
    );
    private final int duration;

    public ClientboundPlayerCombatEndPacket(CombatTracker pCombatTracker) {
        this(pCombatTracker.getCombatDuration());
    }

    public ClientboundPlayerCombatEndPacket(int pDuration) {
        this.duration = pDuration;
    }

    private ClientboundPlayerCombatEndPacket(FriendlyByteBuf pBuffer) {
        this.duration = pBuffer.readVarInt();
    }

    private void m_179043_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.duration);
    }

    @Override
    public PacketType<ClientboundPlayerCombatEndPacket> write() {
        return GamePacketTypes.f_316020_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handlePlayerCombatEnd(this);
    }
}
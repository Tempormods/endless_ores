package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundRespawnPacket(CommonPlayerSpawnInfo commonPlayerSpawnInfo, byte dataToKeep) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundRespawnPacket> f_314716_ = Packet.m_319422_(
        ClientboundRespawnPacket::m_132953_, ClientboundRespawnPacket::new
    );
    public static final byte KEEP_ATTRIBUTES = 1;
    public static final byte KEEP_ENTITY_DATA = 2;
    public static final byte KEEP_ALL_DATA = 3;

    private ClientboundRespawnPacket(RegistryFriendlyByteBuf p_329401_) {
        this(new CommonPlayerSpawnInfo(p_329401_), p_329401_.readByte());
    }

    private void m_132953_(RegistryFriendlyByteBuf p_332270_) {
        this.commonPlayerSpawnInfo.write(p_332270_);
        p_332270_.writeByte(this.dataToKeep);
    }

    @Override
    public PacketType<ClientboundRespawnPacket> write() {
        return GamePacketTypes.f_313964_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleRespawn(this);
    }

    public boolean shouldKeep(byte pData) {
        return (this.dataToKeep & pData) != 0;
    }
}
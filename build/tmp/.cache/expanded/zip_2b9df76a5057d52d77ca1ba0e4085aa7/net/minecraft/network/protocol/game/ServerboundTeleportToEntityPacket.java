package net.minecraft.network.protocol.game;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class ServerboundTeleportToEntityPacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundTeleportToEntityPacket> f_316686_ = Packet.m_319422_(
        ServerboundTeleportToEntityPacket::m_134689_, ServerboundTeleportToEntityPacket::new
    );
    private final UUID uuid;

    public ServerboundTeleportToEntityPacket(UUID pUuid) {
        this.uuid = pUuid;
    }

    private ServerboundTeleportToEntityPacket(FriendlyByteBuf pBuffer) {
        this.uuid = pBuffer.readUUID();
    }

    private void m_134689_(FriendlyByteBuf pBuffer) {
        pBuffer.writeUUID(this.uuid);
    }

    @Override
    public PacketType<ServerboundTeleportToEntityPacket> write() {
        return GamePacketTypes.f_314602_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleTeleportToEntityPacket(this);
    }

    @Nullable
    public Entity getEntity(ServerLevel pLevel) {
        return pLevel.getEntity(this.uuid);
    }
}
package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundEntityEventPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundEntityEventPacket> f_316247_ = Packet.m_319422_(
        ClientboundEntityEventPacket::m_132103_, ClientboundEntityEventPacket::new
    );
    private final int entityId;
    private final byte eventId;

    public ClientboundEntityEventPacket(Entity pEntity, byte pEventId) {
        this.entityId = pEntity.getId();
        this.eventId = pEventId;
    }

    private ClientboundEntityEventPacket(FriendlyByteBuf pBuffer) {
        this.entityId = pBuffer.readInt();
        this.eventId = pBuffer.readByte();
    }

    private void m_132103_(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(this.entityId);
        pBuffer.writeByte(this.eventId);
    }

    @Override
    public PacketType<ClientboundEntityEventPacket> write() {
        return GamePacketTypes.f_316213_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleEntityEvent(this);
    }

    @Nullable
    public Entity getEntity(Level pLevel) {
        return pLevel.getEntity(this.entityId);
    }

    public byte getEventId() {
        return this.eventId;
    }
}
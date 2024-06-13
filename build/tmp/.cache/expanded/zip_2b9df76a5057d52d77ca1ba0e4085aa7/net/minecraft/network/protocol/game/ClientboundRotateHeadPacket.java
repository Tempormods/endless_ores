package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundRotateHeadPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundRotateHeadPacket> f_315782_ = Packet.m_319422_(
        ClientboundRotateHeadPacket::m_132978_, ClientboundRotateHeadPacket::new
    );
    private final int entityId;
    private final byte yHeadRot;

    public ClientboundRotateHeadPacket(Entity pEntity, byte pYHeadRot) {
        this.entityId = pEntity.getId();
        this.yHeadRot = pYHeadRot;
    }

    private ClientboundRotateHeadPacket(FriendlyByteBuf pBuffer) {
        this.entityId = pBuffer.readVarInt();
        this.yHeadRot = pBuffer.readByte();
    }

    private void m_132978_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.entityId);
        pBuffer.writeByte(this.yHeadRot);
    }

    @Override
    public PacketType<ClientboundRotateHeadPacket> write() {
        return GamePacketTypes.f_316835_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleRotateMob(this);
    }

    public Entity getEntity(Level pLevel) {
        return pLevel.getEntity(this.entityId);
    }

    public byte getYHeadRot() {
        return this.yHeadRot;
    }
}
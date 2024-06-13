package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.LivingEntity;

public record ClientboundHurtAnimationPacket(int id, float yaw) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundHurtAnimationPacket> f_314121_ = Packet.m_319422_(
        ClientboundHurtAnimationPacket::m_264237_, ClientboundHurtAnimationPacket::new
    );

    public ClientboundHurtAnimationPacket(LivingEntity pEntity) {
        this(pEntity.getId(), pEntity.getHurtDir());
    }

    private ClientboundHurtAnimationPacket(FriendlyByteBuf pBuffer) {
        this(pBuffer.readVarInt(), pBuffer.readFloat());
    }

    private void m_264237_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.id);
        pBuffer.writeFloat(this.yaw);
    }

    @Override
    public PacketType<ClientboundHurtAnimationPacket> write() {
        return GamePacketTypes.f_315718_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleHurtAnimation(this);
    }
}
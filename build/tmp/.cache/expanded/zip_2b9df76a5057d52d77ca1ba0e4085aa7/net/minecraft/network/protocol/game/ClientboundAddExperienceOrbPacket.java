package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.ExperienceOrb;

public class ClientboundAddExperienceOrbPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundAddExperienceOrbPacket> f_313959_ = Packet.m_319422_(
        ClientboundAddExperienceOrbPacket::m_131525_, ClientboundAddExperienceOrbPacket::new
    );
    private final int id;
    private final double x;
    private final double y;
    private final double z;
    private final int value;

    public ClientboundAddExperienceOrbPacket(ExperienceOrb pOrbEntity) {
        this.id = pOrbEntity.getId();
        this.x = pOrbEntity.getX();
        this.y = pOrbEntity.getY();
        this.z = pOrbEntity.getZ();
        this.value = pOrbEntity.getValue();
    }

    private ClientboundAddExperienceOrbPacket(FriendlyByteBuf pBuffer) {
        this.id = pBuffer.readVarInt();
        this.x = pBuffer.readDouble();
        this.y = pBuffer.readDouble();
        this.z = pBuffer.readDouble();
        this.value = pBuffer.readShort();
    }

    private void m_131525_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.id);
        pBuffer.writeDouble(this.x);
        pBuffer.writeDouble(this.y);
        pBuffer.writeDouble(this.z);
        pBuffer.writeShort(this.value);
    }

    @Override
    public PacketType<ClientboundAddExperienceOrbPacket> write() {
        return GamePacketTypes.f_316938_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleAddExperienceOrb(this);
    }

    public int getId() {
        return this.id;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public int getValue() {
        return this.value;
    }
}
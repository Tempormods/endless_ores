package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;

public class ClientboundMoveVehiclePacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundMoveVehiclePacket> f_314203_ = Packet.m_319422_(
        ClientboundMoveVehiclePacket::m_132592_, ClientboundMoveVehiclePacket::new
    );
    private final double x;
    private final double y;
    private final double z;
    private final float yRot;
    private final float xRot;

    public ClientboundMoveVehiclePacket(Entity pVehicle) {
        this.x = pVehicle.getX();
        this.y = pVehicle.getY();
        this.z = pVehicle.getZ();
        this.yRot = pVehicle.getYRot();
        this.xRot = pVehicle.getXRot();
    }

    private ClientboundMoveVehiclePacket(FriendlyByteBuf pBuffer) {
        this.x = pBuffer.readDouble();
        this.y = pBuffer.readDouble();
        this.z = pBuffer.readDouble();
        this.yRot = pBuffer.readFloat();
        this.xRot = pBuffer.readFloat();
    }

    private void m_132592_(FriendlyByteBuf pBuffer) {
        pBuffer.writeDouble(this.x);
        pBuffer.writeDouble(this.y);
        pBuffer.writeDouble(this.z);
        pBuffer.writeFloat(this.yRot);
        pBuffer.writeFloat(this.xRot);
    }

    @Override
    public PacketType<ClientboundMoveVehiclePacket> write() {
        return GamePacketTypes.f_315871_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleMoveVehicle(this);
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

    public float getYRot() {
        return this.yRot;
    }

    public float getXRot() {
        return this.xRot;
    }
}
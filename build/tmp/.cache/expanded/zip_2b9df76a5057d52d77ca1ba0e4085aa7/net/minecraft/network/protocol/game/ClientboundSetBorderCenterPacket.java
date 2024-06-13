package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderCenterPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundSetBorderCenterPacket> f_314354_ = Packet.m_319422_(
        ClientboundSetBorderCenterPacket::m_179217_, ClientboundSetBorderCenterPacket::new
    );
    private final double newCenterX;
    private final double newCenterZ;

    public ClientboundSetBorderCenterPacket(WorldBorder pWorldBorder) {
        this.newCenterX = pWorldBorder.getCenterX();
        this.newCenterZ = pWorldBorder.getCenterZ();
    }

    private ClientboundSetBorderCenterPacket(FriendlyByteBuf pBuffer) {
        this.newCenterX = pBuffer.readDouble();
        this.newCenterZ = pBuffer.readDouble();
    }

    private void m_179217_(FriendlyByteBuf pBuffer) {
        pBuffer.writeDouble(this.newCenterX);
        pBuffer.writeDouble(this.newCenterZ);
    }

    @Override
    public PacketType<ClientboundSetBorderCenterPacket> write() {
        return GamePacketTypes.f_314106_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleSetBorderCenter(this);
    }

    public double getNewCenterZ() {
        return this.newCenterZ;
    }

    public double getNewCenterX() {
        return this.newCenterX;
    }
}
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundContainerSetDataPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundContainerSetDataPacket> f_315733_ = Packet.m_319422_(
        ClientboundContainerSetDataPacket::m_131973_, ClientboundContainerSetDataPacket::new
    );
    private final int containerId;
    private final int id;
    private final int value;

    public ClientboundContainerSetDataPacket(int pContainerId, int pId, int pValue) {
        this.containerId = pContainerId;
        this.id = pId;
        this.value = pValue;
    }

    private ClientboundContainerSetDataPacket(FriendlyByteBuf pBuffer) {
        this.containerId = pBuffer.readUnsignedByte();
        this.id = pBuffer.readShort();
        this.value = pBuffer.readShort();
    }

    private void m_131973_(FriendlyByteBuf pBuffer) {
        pBuffer.writeByte(this.containerId);
        pBuffer.writeShort(this.id);
        pBuffer.writeShort(this.value);
    }

    @Override
    public PacketType<ClientboundContainerSetDataPacket> write() {
        return GamePacketTypes.f_314542_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleContainerSetData(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public int getId() {
        return this.id;
    }

    public int getValue() {
        return this.value;
    }
}
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundContainerClosePacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundContainerClosePacket> f_316790_ = Packet.m_319422_(
        ClientboundContainerClosePacket::m_131940_, ClientboundContainerClosePacket::new
    );
    private final int containerId;

    public ClientboundContainerClosePacket(int pContainerId) {
        this.containerId = pContainerId;
    }

    private ClientboundContainerClosePacket(FriendlyByteBuf pBuffer) {
        this.containerId = pBuffer.readUnsignedByte();
    }

    private void m_131940_(FriendlyByteBuf pBuffer) {
        pBuffer.writeByte(this.containerId);
    }

    @Override
    public PacketType<ClientboundContainerClosePacket> write() {
        return GamePacketTypes.f_316559_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleContainerClose(this);
    }

    public int getContainerId() {
        return this.containerId;
    }
}
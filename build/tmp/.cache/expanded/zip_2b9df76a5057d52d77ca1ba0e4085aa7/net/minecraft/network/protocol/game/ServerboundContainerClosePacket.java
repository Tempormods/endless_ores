package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundContainerClosePacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundContainerClosePacket> f_315426_ = Packet.m_319422_(
        ServerboundContainerClosePacket::m_133977_, ServerboundContainerClosePacket::new
    );
    private final int containerId;

    public ServerboundContainerClosePacket(int pContainerId) {
        this.containerId = pContainerId;
    }

    private ServerboundContainerClosePacket(FriendlyByteBuf pBuffer) {
        this.containerId = pBuffer.readByte();
    }

    private void m_133977_(FriendlyByteBuf pBuffer) {
        pBuffer.writeByte(this.containerId);
    }

    @Override
    public PacketType<ServerboundContainerClosePacket> write() {
        return GamePacketTypes.f_315682_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleContainerClose(this);
    }

    public int getContainerId() {
        return this.containerId;
    }
}
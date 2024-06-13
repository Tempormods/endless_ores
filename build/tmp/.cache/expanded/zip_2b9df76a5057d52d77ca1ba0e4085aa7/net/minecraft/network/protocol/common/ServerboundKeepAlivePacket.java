package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundKeepAlivePacket implements Packet<ServerCommonPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundKeepAlivePacket> f_315984_ = Packet.m_319422_(
        ServerboundKeepAlivePacket::m_295766_, ServerboundKeepAlivePacket::new
    );
    private final long id;

    public ServerboundKeepAlivePacket(long pId) {
        this.id = pId;
    }

    private ServerboundKeepAlivePacket(FriendlyByteBuf pBuffer) {
        this.id = pBuffer.readLong();
    }

    private void m_295766_(FriendlyByteBuf pBuffer) {
        pBuffer.writeLong(this.id);
    }

    @Override
    public PacketType<ServerboundKeepAlivePacket> write() {
        return CommonPacketTypes.f_316196_;
    }

    public void handle(ServerCommonPacketListener pHandler) {
        pHandler.handleKeepAlive(this);
    }

    public long getId() {
        return this.id;
    }
}
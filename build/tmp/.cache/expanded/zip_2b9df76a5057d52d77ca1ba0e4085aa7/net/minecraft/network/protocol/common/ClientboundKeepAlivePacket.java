package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundKeepAlivePacket implements Packet<ClientCommonPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundKeepAlivePacket> f_314816_ = Packet.m_319422_(
        ClientboundKeepAlivePacket::m_294131_, ClientboundKeepAlivePacket::new
    );
    private final long id;

    public ClientboundKeepAlivePacket(long pId) {
        this.id = pId;
    }

    private ClientboundKeepAlivePacket(FriendlyByteBuf pBuffer) {
        this.id = pBuffer.readLong();
    }

    private void m_294131_(FriendlyByteBuf pBuffer) {
        pBuffer.writeLong(this.id);
    }

    @Override
    public PacketType<ClientboundKeepAlivePacket> write() {
        return CommonPacketTypes.f_314082_;
    }

    public void handle(ClientCommonPacketListener pHandler) {
        pHandler.handleKeepAlive(this);
    }

    public long getId() {
        return this.id;
    }
}
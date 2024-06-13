package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundPingPacket implements Packet<ClientCommonPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundPingPacket> f_314346_ = Packet.m_319422_(
        ClientboundPingPacket::m_295201_, ClientboundPingPacket::new
    );
    private final int id;

    public ClientboundPingPacket(int pId) {
        this.id = pId;
    }

    private ClientboundPingPacket(FriendlyByteBuf pBuffer) {
        this.id = pBuffer.readInt();
    }

    private void m_295201_(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(this.id);
    }

    @Override
    public PacketType<ClientboundPingPacket> write() {
        return CommonPacketTypes.f_316690_;
    }

    public void handle(ClientCommonPacketListener pHandler) {
        pHandler.handlePing(this);
    }

    public int getId() {
        return this.id;
    }
}
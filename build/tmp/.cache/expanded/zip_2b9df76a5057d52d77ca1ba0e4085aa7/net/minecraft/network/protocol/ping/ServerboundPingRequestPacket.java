package net.minecraft.network.protocol.ping;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundPingRequestPacket implements Packet<ServerPingPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundPingRequestPacket> f_314691_ = Packet.m_319422_(
        ServerboundPingRequestPacket::m_324694_, ServerboundPingRequestPacket::new
    );
    private final long f_316851_;

    public ServerboundPingRequestPacket(long p_333024_) {
        this.f_316851_ = p_333024_;
    }

    private ServerboundPingRequestPacket(FriendlyByteBuf p_335917_) {
        this.f_316851_ = p_335917_.readLong();
    }

    private void m_324694_(FriendlyByteBuf p_334813_) {
        p_334813_.writeLong(this.f_316851_);
    }

    @Override
    public PacketType<ServerboundPingRequestPacket> write() {
        return PingPacketTypes.f_314322_;
    }

    public void handle(ServerPingPacketListener p_336205_) {
        p_336205_.handlePingRequest(this);
    }

    public long m_324239_() {
        return this.f_316851_;
    }
}
package net.minecraft.network.protocol.ping;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundPongResponsePacket(long f_315106_) implements Packet<ClientPongPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundPongResponsePacket> f_314698_ = Packet.m_319422_(
        ClientboundPongResponsePacket::m_321010_, ClientboundPongResponsePacket::new
    );

    private ClientboundPongResponsePacket(FriendlyByteBuf p_334575_) {
        this(p_334575_.readLong());
    }

    private void m_321010_(FriendlyByteBuf p_335126_) {
        p_335126_.writeLong(this.f_315106_);
    }

    @Override
    public PacketType<ClientboundPongResponsePacket> write() {
        return PingPacketTypes.f_316097_;
    }

    public void handle(ClientPongPacketListener p_332635_) {
        p_332635_.handlePongResponse(this);
    }
}
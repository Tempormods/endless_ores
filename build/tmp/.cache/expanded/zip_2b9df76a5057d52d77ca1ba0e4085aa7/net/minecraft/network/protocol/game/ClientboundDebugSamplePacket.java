package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.debugchart.RemoteDebugSampleType;

public record ClientboundDebugSamplePacket(long[] f_315508_, RemoteDebugSampleType f_316193_) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundDebugSamplePacket> f_316133_ = Packet.m_319422_(
        ClientboundDebugSamplePacket::m_319070_, ClientboundDebugSamplePacket::new
    );

    private ClientboundDebugSamplePacket(FriendlyByteBuf p_330326_) {
        this(p_330326_.readLongArray(), p_330326_.readEnum(RemoteDebugSampleType.class));
    }

    private void m_319070_(FriendlyByteBuf p_330431_) {
        p_330431_.writeLongArray(this.f_315508_);
        p_330431_.writeEnum(this.f_316193_);
    }

    @Override
    public PacketType<ClientboundDebugSamplePacket> write() {
        return GamePacketTypes.f_316726_;
    }

    public void handle(ClientGamePacketListener p_330875_) {
        p_330875_.m_319072_(this);
    }
}
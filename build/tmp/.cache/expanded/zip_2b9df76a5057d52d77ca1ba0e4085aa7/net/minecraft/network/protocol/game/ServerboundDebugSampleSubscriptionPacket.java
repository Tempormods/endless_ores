package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.debugchart.RemoteDebugSampleType;

public record ServerboundDebugSampleSubscriptionPacket(RemoteDebugSampleType f_314616_) implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundDebugSampleSubscriptionPacket> f_314211_ = Packet.m_319422_(
        ServerboundDebugSampleSubscriptionPacket::m_318928_, ServerboundDebugSampleSubscriptionPacket::new
    );

    private ServerboundDebugSampleSubscriptionPacket(FriendlyByteBuf p_329344_) {
        this(p_329344_.readEnum(RemoteDebugSampleType.class));
    }

    private void m_318928_(FriendlyByteBuf p_329925_) {
        p_329925_.writeEnum(this.f_314616_);
    }

    @Override
    public PacketType<ServerboundDebugSampleSubscriptionPacket> write() {
        return GamePacketTypes.f_315236_;
    }

    public void handle(ServerGamePacketListener p_335058_) {
        p_335058_.m_318909_(this);
    }
}
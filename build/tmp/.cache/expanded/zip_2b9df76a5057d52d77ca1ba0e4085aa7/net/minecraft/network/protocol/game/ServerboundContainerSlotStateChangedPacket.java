package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundContainerSlotStateChangedPacket(int f_303719_, int f_303525_, boolean f_303711_) implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundContainerSlotStateChangedPacket> f_314655_ = Packet.m_319422_(
        ServerboundContainerSlotStateChangedPacket::m_307495_, ServerboundContainerSlotStateChangedPacket::new
    );

    private ServerboundContainerSlotStateChangedPacket(FriendlyByteBuf p_312822_) {
        this(p_312822_.readVarInt(), p_312822_.readVarInt(), p_312822_.readBoolean());
    }

    private void m_307495_(FriendlyByteBuf p_310021_) {
        p_310021_.writeVarInt(this.f_303719_);
        p_310021_.writeVarInt(this.f_303525_);
        p_310021_.writeBoolean(this.f_303711_);
    }

    @Override
    public PacketType<ServerboundContainerSlotStateChangedPacket> write() {
        return GamePacketTypes.f_314574_;
    }

    public void handle(ServerGamePacketListener p_309835_) {
        p_309835_.m_305984_(this);
    }
}
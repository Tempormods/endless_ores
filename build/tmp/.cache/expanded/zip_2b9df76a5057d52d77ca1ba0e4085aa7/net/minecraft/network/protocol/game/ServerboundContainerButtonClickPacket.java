package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundContainerButtonClickPacket(int containerId, int buttonId) implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundContainerButtonClickPacket> f_316578_ = StreamCodec.m_320349_(
        ByteBufCodecs.f_316730_,
        ServerboundContainerButtonClickPacket::containerId,
        ByteBufCodecs.f_316730_,
        ServerboundContainerButtonClickPacket::buttonId,
        ServerboundContainerButtonClickPacket::new
    );

    @Override
    public PacketType<ServerboundContainerButtonClickPacket> write() {
        return GamePacketTypes.f_316494_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleContainerButtonClick(this);
    }
}
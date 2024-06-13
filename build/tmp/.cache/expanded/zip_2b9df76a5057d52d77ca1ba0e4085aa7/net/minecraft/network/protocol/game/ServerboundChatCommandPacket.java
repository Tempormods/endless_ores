package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundChatCommandPacket(String command) implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundChatCommandPacket> f_316859_ = Packet.m_319422_(
        ServerboundChatCommandPacket::m_237935_, ServerboundChatCommandPacket::new
    );

    private ServerboundChatCommandPacket(FriendlyByteBuf pBuffer) {
        this(pBuffer.readUtf());
    }

    private void m_237935_(FriendlyByteBuf pBuffer) {
        pBuffer.writeUtf(this.command);
    }

    @Override
    public PacketType<ServerboundChatCommandPacket> write() {
        return GamePacketTypes.f_315003_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleChatCommand(this);
    }
}
package net.minecraft.network.protocol.login;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundHelloPacket(String name, UUID profileId) implements Packet<ServerLoginPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundHelloPacket> f_316512_ = Packet.m_319422_(
        ServerboundHelloPacket::m_134850_, ServerboundHelloPacket::new
    );

    private ServerboundHelloPacket(FriendlyByteBuf pBuffer) {
        this(pBuffer.readUtf(16), pBuffer.readUUID());
    }

    private void m_134850_(FriendlyByteBuf pBuffer) {
        pBuffer.writeUtf(this.name, 16);
        pBuffer.writeUUID(this.profileId);
    }

    @Override
    public PacketType<ServerboundHelloPacket> write() {
        return LoginPacketTypes.f_315312_;
    }

    public void handle(ServerLoginPacketListener pHandler) {
        pHandler.handleHello(this);
    }
}
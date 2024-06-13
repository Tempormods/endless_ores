package net.minecraft.network.protocol.login;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundLoginDisconnectPacket implements Packet<ClientLoginPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundLoginDisconnectPacket> f_314580_ = Packet.m_319422_(
        ClientboundLoginDisconnectPacket::m_134820_, ClientboundLoginDisconnectPacket::new
    );
    private final Component reason;

    public ClientboundLoginDisconnectPacket(Component pReason) {
        this.reason = pReason;
    }

    private ClientboundLoginDisconnectPacket(FriendlyByteBuf pBuffer) {
        this.reason = Component.Serializer.fromJsonLenient(pBuffer.readUtf(262144), RegistryAccess.EMPTY);
    }

    private void m_134820_(FriendlyByteBuf pBuffer) {
        pBuffer.writeUtf(Component.Serializer.toJson(this.reason, RegistryAccess.EMPTY));
    }

    @Override
    public PacketType<ClientboundLoginDisconnectPacket> write() {
        return LoginPacketTypes.f_314916_;
    }

    public void handle(ClientLoginPacketListener pHandler) {
        pHandler.handleDisconnect(this);
    }

    public Component getReason() {
        return this.reason;
    }
}
package net.minecraft.network.protocol.login;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundLoginCompressionPacket implements Packet<ClientLoginPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundLoginCompressionPacket> f_314441_ = Packet.m_319422_(
        ClientboundLoginCompressionPacket::m_134807_, ClientboundLoginCompressionPacket::new
    );
    private final int compressionThreshold;

    public ClientboundLoginCompressionPacket(int pCompressionThreshold) {
        this.compressionThreshold = pCompressionThreshold;
    }

    private ClientboundLoginCompressionPacket(FriendlyByteBuf pBuffer) {
        this.compressionThreshold = pBuffer.readVarInt();
    }

    private void m_134807_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.compressionThreshold);
    }

    @Override
    public PacketType<ClientboundLoginCompressionPacket> write() {
        return LoginPacketTypes.f_315271_;
    }

    public void handle(ClientLoginPacketListener pHandler) {
        pHandler.handleCompression(this);
    }

    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }
}
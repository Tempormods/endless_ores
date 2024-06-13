package net.minecraft.network.protocol.login;

import java.security.PublicKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;

public class ClientboundHelloPacket implements Packet<ClientLoginPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundHelloPacket> f_317006_ = Packet.m_319422_(
        ClientboundHelloPacket::m_134792_, ClientboundHelloPacket::new
    );
    private final String serverId;
    private final byte[] publicKey;
    private final byte[] challenge;
    private final boolean f_314872_;

    public ClientboundHelloPacket(String pServerId, byte[] pPublicKey, byte[] pChallenge, boolean p_331026_) {
        this.serverId = pServerId;
        this.publicKey = pPublicKey;
        this.challenge = pChallenge;
        this.f_314872_ = p_331026_;
    }

    private ClientboundHelloPacket(FriendlyByteBuf pBuffer) {
        this.serverId = pBuffer.readUtf(20);
        this.publicKey = pBuffer.readByteArray();
        this.challenge = pBuffer.readByteArray();
        this.f_314872_ = pBuffer.readBoolean();
    }

    private void m_134792_(FriendlyByteBuf pBuffer) {
        pBuffer.writeUtf(this.serverId);
        pBuffer.writeByteArray(this.publicKey);
        pBuffer.writeByteArray(this.challenge);
        pBuffer.writeBoolean(this.f_314872_);
    }

    @Override
    public PacketType<ClientboundHelloPacket> write() {
        return LoginPacketTypes.f_314646_;
    }

    public void handle(ClientLoginPacketListener pHandler) {
        pHandler.handleHello(this);
    }

    public String getServerId() {
        return this.serverId;
    }

    public PublicKey getPublicKey() throws CryptException {
        return Crypt.byteToPublicKey(this.publicKey);
    }

    public byte[] getChallenge() {
        return this.challenge;
    }

    public boolean m_319283_() {
        return this.f_314872_;
    }
}
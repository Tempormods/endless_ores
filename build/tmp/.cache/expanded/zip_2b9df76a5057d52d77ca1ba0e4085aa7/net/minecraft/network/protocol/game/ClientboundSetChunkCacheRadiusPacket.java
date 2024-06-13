package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundSetChunkCacheRadiusPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundSetChunkCacheRadiusPacket> f_315586_ = Packet.m_319422_(
        ClientboundSetChunkCacheRadiusPacket::m_133109_, ClientboundSetChunkCacheRadiusPacket::new
    );
    private final int radius;

    public ClientboundSetChunkCacheRadiusPacket(int pRadius) {
        this.radius = pRadius;
    }

    private ClientboundSetChunkCacheRadiusPacket(FriendlyByteBuf pBuffer) {
        this.radius = pBuffer.readVarInt();
    }

    private void m_133109_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.radius);
    }

    @Override
    public PacketType<ClientboundSetChunkCacheRadiusPacket> write() {
        return GamePacketTypes.f_314887_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleSetChunkCacheRadius(this);
    }

    public int getRadius() {
        return this.radius;
    }
}
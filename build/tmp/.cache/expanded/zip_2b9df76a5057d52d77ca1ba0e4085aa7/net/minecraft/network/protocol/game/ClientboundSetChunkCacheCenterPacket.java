package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundSetChunkCacheCenterPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundSetChunkCacheCenterPacket> f_316858_ = Packet.m_319422_(
        ClientboundSetChunkCacheCenterPacket::m_133095_, ClientboundSetChunkCacheCenterPacket::new
    );
    private final int x;
    private final int z;

    public ClientboundSetChunkCacheCenterPacket(int pX, int pZ) {
        this.x = pX;
        this.z = pZ;
    }

    private ClientboundSetChunkCacheCenterPacket(FriendlyByteBuf pBuffer) {
        this.x = pBuffer.readVarInt();
        this.z = pBuffer.readVarInt();
    }

    private void m_133095_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.x);
        pBuffer.writeVarInt(this.z);
    }

    @Override
    public PacketType<ClientboundSetChunkCacheCenterPacket> write() {
        return GamePacketTypes.f_314771_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleSetChunkCacheCenter(this);
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }
}
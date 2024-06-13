package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderSizePacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundSetBorderSizePacket> f_316063_ = Packet.m_319422_(
        ClientboundSetBorderSizePacket::m_179246_, ClientboundSetBorderSizePacket::new
    );
    private final double size;

    public ClientboundSetBorderSizePacket(WorldBorder pWorldBorder) {
        this.size = pWorldBorder.getLerpTarget();
    }

    private ClientboundSetBorderSizePacket(FriendlyByteBuf pBuffer) {
        this.size = pBuffer.readDouble();
    }

    private void m_179246_(FriendlyByteBuf pBuffer) {
        pBuffer.writeDouble(this.size);
    }

    @Override
    public PacketType<ClientboundSetBorderSizePacket> write() {
        return GamePacketTypes.f_315074_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleSetBorderSize(this);
    }

    public double getSize() {
        return this.size;
    }
}
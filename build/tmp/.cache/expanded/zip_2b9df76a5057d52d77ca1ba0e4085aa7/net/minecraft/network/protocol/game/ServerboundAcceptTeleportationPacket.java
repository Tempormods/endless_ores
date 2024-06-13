package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundAcceptTeleportationPacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundAcceptTeleportationPacket> f_317145_ = Packet.m_319422_(
        ServerboundAcceptTeleportationPacket::m_133796_, ServerboundAcceptTeleportationPacket::new
    );
    private final int id;

    public ServerboundAcceptTeleportationPacket(int pId) {
        this.id = pId;
    }

    private ServerboundAcceptTeleportationPacket(FriendlyByteBuf pBuffer) {
        this.id = pBuffer.readVarInt();
    }

    private void m_133796_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.id);
    }

    @Override
    public PacketType<ServerboundAcceptTeleportationPacket> write() {
        return GamePacketTypes.f_314386_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleAcceptTeleportPacket(this);
    }

    public int getId() {
        return this.id;
    }
}
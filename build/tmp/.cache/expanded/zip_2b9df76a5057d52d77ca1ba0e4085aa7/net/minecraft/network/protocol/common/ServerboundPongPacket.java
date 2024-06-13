package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundPongPacket implements Packet<ServerCommonPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundPongPacket> f_315035_ = Packet.m_319422_(
        ServerboundPongPacket::m_294446_, ServerboundPongPacket::new
    );
    private final int id;

    public ServerboundPongPacket(int pId) {
        this.id = pId;
    }

    private ServerboundPongPacket(FriendlyByteBuf pBuffer) {
        this.id = pBuffer.readInt();
    }

    private void m_294446_(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(this.id);
    }

    @Override
    public PacketType<ServerboundPongPacket> write() {
        return CommonPacketTypes.f_316824_;
    }

    public void handle(ServerCommonPacketListener pHandler) {
        pHandler.handlePong(this);
    }

    public int getId() {
        return this.id;
    }
}
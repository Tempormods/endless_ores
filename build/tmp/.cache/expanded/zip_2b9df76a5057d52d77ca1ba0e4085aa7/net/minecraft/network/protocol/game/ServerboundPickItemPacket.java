package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundPickItemPacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundPickItemPacket> f_315445_ = Packet.m_319422_(
        ServerboundPickItemPacket::m_134233_, ServerboundPickItemPacket::new
    );
    private final int slot;

    public ServerboundPickItemPacket(int pSlot) {
        this.slot = pSlot;
    }

    private ServerboundPickItemPacket(FriendlyByteBuf pBuffer) {
        this.slot = pBuffer.readVarInt();
    }

    private void m_134233_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.slot);
    }

    @Override
    public PacketType<ServerboundPickItemPacket> write() {
        return GamePacketTypes.f_317093_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handlePickItem(this);
    }

    public int getSlot() {
        return this.slot;
    }
}
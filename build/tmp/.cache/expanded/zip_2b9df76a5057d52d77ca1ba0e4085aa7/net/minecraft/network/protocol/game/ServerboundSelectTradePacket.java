package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundSelectTradePacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundSelectTradePacket> f_315957_ = Packet.m_319422_(
        ServerboundSelectTradePacket::m_134470_, ServerboundSelectTradePacket::new
    );
    private final int item;

    public ServerboundSelectTradePacket(int pItem) {
        this.item = pItem;
    }

    private ServerboundSelectTradePacket(FriendlyByteBuf pBuffer) {
        this.item = pBuffer.readVarInt();
    }

    private void m_134470_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.item);
    }

    @Override
    public PacketType<ServerboundSelectTradePacket> write() {
        return GamePacketTypes.f_315356_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleSelectTrade(this);
    }

    public int getItem() {
        return this.item;
    }
}
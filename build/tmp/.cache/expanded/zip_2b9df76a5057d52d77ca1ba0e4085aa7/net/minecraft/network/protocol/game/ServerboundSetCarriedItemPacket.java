package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundSetCarriedItemPacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundSetCarriedItemPacket> f_314858_ = Packet.m_319422_(
        ServerboundSetCarriedItemPacket::m_134499_, ServerboundSetCarriedItemPacket::new
    );
    private final int slot;

    public ServerboundSetCarriedItemPacket(int pSlot) {
        this.slot = pSlot;
    }

    private ServerboundSetCarriedItemPacket(FriendlyByteBuf pBuffer) {
        this.slot = pBuffer.readShort();
    }

    private void m_134499_(FriendlyByteBuf pBuffer) {
        pBuffer.writeShort(this.slot);
    }

    @Override
    public PacketType<ServerboundSetCarriedItemPacket> write() {
        return GamePacketTypes.f_316312_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleSetCarriedItem(this);
    }

    public int getSlot() {
        return this.slot;
    }
}
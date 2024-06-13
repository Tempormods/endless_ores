package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundSetCarriedItemPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundSetCarriedItemPacket> f_315552_ = Packet.m_319422_(
        ClientboundSetCarriedItemPacket::m_133080_, ClientboundSetCarriedItemPacket::new
    );
    private final int slot;

    public ClientboundSetCarriedItemPacket(int pSlot) {
        this.slot = pSlot;
    }

    private ClientboundSetCarriedItemPacket(FriendlyByteBuf pBuffer) {
        this.slot = pBuffer.readByte();
    }

    private void m_133080_(FriendlyByteBuf pBuffer) {
        pBuffer.writeByte(this.slot);
    }

    @Override
    public PacketType<ClientboundSetCarriedItemPacket> write() {
        return GamePacketTypes.f_315363_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleSetCarriedItem(this);
    }

    public int getSlot() {
        return this.slot;
    }
}
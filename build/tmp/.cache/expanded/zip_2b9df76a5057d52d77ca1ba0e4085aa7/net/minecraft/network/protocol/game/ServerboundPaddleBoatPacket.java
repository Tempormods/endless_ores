package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundPaddleBoatPacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundPaddleBoatPacket> f_316326_ = Packet.m_319422_(
        ServerboundPaddleBoatPacket::m_134219_, ServerboundPaddleBoatPacket::new
    );
    private final boolean left;
    private final boolean right;

    public ServerboundPaddleBoatPacket(boolean pLeft, boolean pRight) {
        this.left = pLeft;
        this.right = pRight;
    }

    private ServerboundPaddleBoatPacket(FriendlyByteBuf pBuffer) {
        this.left = pBuffer.readBoolean();
        this.right = pBuffer.readBoolean();
    }

    private void m_134219_(FriendlyByteBuf pBuffer) {
        pBuffer.writeBoolean(this.left);
        pBuffer.writeBoolean(this.right);
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handlePaddleBoat(this);
    }

    @Override
    public PacketType<ServerboundPaddleBoatPacket> write() {
        return GamePacketTypes.f_316162_;
    }

    public boolean getLeft() {
        return this.left;
    }

    public boolean getRight() {
        return this.right;
    }
}
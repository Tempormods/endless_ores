package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.InteractionHand;

public class ServerboundSwingPacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundSwingPacket> f_315791_ = Packet.m_319422_(
        ServerboundSwingPacket::m_134675_, ServerboundSwingPacket::new
    );
    private final InteractionHand hand;

    public ServerboundSwingPacket(InteractionHand pHand) {
        this.hand = pHand;
    }

    private ServerboundSwingPacket(FriendlyByteBuf pBuffer) {
        this.hand = pBuffer.readEnum(InteractionHand.class);
    }

    private void m_134675_(FriendlyByteBuf pBuffer) {
        pBuffer.writeEnum(this.hand);
    }

    @Override
    public PacketType<ServerboundSwingPacket> write() {
        return GamePacketTypes.f_315113_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleAnimate(this);
    }

    public InteractionHand getHand() {
        return this.hand;
    }
}
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.InteractionHand;

public class ClientboundOpenBookPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundOpenBookPacket> f_316297_ = Packet.m_319422_(
        ClientboundOpenBookPacket::m_132609_, ClientboundOpenBookPacket::new
    );
    private final InteractionHand hand;

    public ClientboundOpenBookPacket(InteractionHand pHand) {
        this.hand = pHand;
    }

    private ClientboundOpenBookPacket(FriendlyByteBuf pBuffer) {
        this.hand = pBuffer.readEnum(InteractionHand.class);
    }

    private void m_132609_(FriendlyByteBuf pBuffer) {
        pBuffer.writeEnum(this.hand);
    }

    @Override
    public PacketType<ClientboundOpenBookPacket> write() {
        return GamePacketTypes.f_314099_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleOpenBook(this);
    }

    public InteractionHand getHand() {
        return this.hand;
    }
}
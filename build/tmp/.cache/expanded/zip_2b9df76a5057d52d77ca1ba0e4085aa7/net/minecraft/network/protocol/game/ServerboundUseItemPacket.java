package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.InteractionHand;

public class ServerboundUseItemPacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundUseItemPacket> f_316479_ = Packet.m_319422_(
        ServerboundUseItemPacket::m_134718_, ServerboundUseItemPacket::new
    );
    private final InteractionHand hand;
    private final int sequence;

    public ServerboundUseItemPacket(InteractionHand pHand, int pSequence) {
        this.hand = pHand;
        this.sequence = pSequence;
    }

    private ServerboundUseItemPacket(FriendlyByteBuf pBuffer) {
        this.hand = pBuffer.readEnum(InteractionHand.class);
        this.sequence = pBuffer.readVarInt();
    }

    private void m_134718_(FriendlyByteBuf pBuffer) {
        pBuffer.writeEnum(this.hand);
        pBuffer.writeVarInt(this.sequence);
    }

    @Override
    public PacketType<ServerboundUseItemPacket> write() {
        return GamePacketTypes.f_316696_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleUseItem(this);
    }

    public InteractionHand getHand() {
        return this.hand;
    }

    public int getSequence() {
        return this.sequence;
    }
}
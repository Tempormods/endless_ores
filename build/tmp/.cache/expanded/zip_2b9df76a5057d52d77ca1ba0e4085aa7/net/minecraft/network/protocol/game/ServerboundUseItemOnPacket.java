package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;

public class ServerboundUseItemOnPacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundUseItemOnPacket> f_316673_ = Packet.m_319422_(
        ServerboundUseItemOnPacket::m_134704_, ServerboundUseItemOnPacket::new
    );
    private final BlockHitResult blockHit;
    private final InteractionHand hand;
    private final int sequence;

    public ServerboundUseItemOnPacket(InteractionHand pHand, BlockHitResult pBlockHit, int pSequence) {
        this.hand = pHand;
        this.blockHit = pBlockHit;
        this.sequence = pSequence;
    }

    private ServerboundUseItemOnPacket(FriendlyByteBuf pBuffer) {
        this.hand = pBuffer.readEnum(InteractionHand.class);
        this.blockHit = pBuffer.readBlockHitResult();
        this.sequence = pBuffer.readVarInt();
    }

    private void m_134704_(FriendlyByteBuf pBuffer) {
        pBuffer.writeEnum(this.hand);
        pBuffer.writeBlockHitResult(this.blockHit);
        pBuffer.writeVarInt(this.sequence);
    }

    @Override
    public PacketType<ServerboundUseItemOnPacket> write() {
        return GamePacketTypes.f_316260_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleUseItemOn(this);
    }

    public InteractionHand getHand() {
        return this.hand;
    }

    public BlockHitResult getHitResult() {
        return this.blockHit;
    }

    public int getSequence() {
        return this.sequence;
    }
}
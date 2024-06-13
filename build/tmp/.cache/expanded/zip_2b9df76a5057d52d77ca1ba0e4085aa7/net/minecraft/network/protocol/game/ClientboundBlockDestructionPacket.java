package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundBlockDestructionPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundBlockDestructionPacket> f_316887_ = Packet.m_319422_(
        ClientboundBlockDestructionPacket::m_131686_, ClientboundBlockDestructionPacket::new
    );
    private final int id;
    private final BlockPos pos;
    private final int progress;

    public ClientboundBlockDestructionPacket(int pId, BlockPos pPos, int pProgress) {
        this.id = pId;
        this.pos = pPos;
        this.progress = pProgress;
    }

    private ClientboundBlockDestructionPacket(FriendlyByteBuf pBuffer) {
        this.id = pBuffer.readVarInt();
        this.pos = pBuffer.readBlockPos();
        this.progress = pBuffer.readUnsignedByte();
    }

    private void m_131686_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.id);
        pBuffer.writeBlockPos(this.pos);
        pBuffer.writeByte(this.progress);
    }

    @Override
    public PacketType<ClientboundBlockDestructionPacket> write() {
        return GamePacketTypes.f_316424_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleBlockDestruction(this);
    }

    public int getId() {
        return this.id;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getProgress() {
        return this.progress;
    }
}
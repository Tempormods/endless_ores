package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundTagQueryPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundTagQueryPacket> f_315596_ = Packet.m_319422_(
        ClientboundTagQueryPacket::m_133507_, ClientboundTagQueryPacket::new
    );
    private final int transactionId;
    @Nullable
    private final CompoundTag tag;

    public ClientboundTagQueryPacket(int pTransactionId, @Nullable CompoundTag pTag) {
        this.transactionId = pTransactionId;
        this.tag = pTag;
    }

    private ClientboundTagQueryPacket(FriendlyByteBuf pBuffer) {
        this.transactionId = pBuffer.readVarInt();
        this.tag = pBuffer.readNbt();
    }

    private void m_133507_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.transactionId);
        pBuffer.writeNbt(this.tag);
    }

    @Override
    public PacketType<ClientboundTagQueryPacket> write() {
        return GamePacketTypes.f_315760_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleTagQueryPacket(this);
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    @Nullable
    public CompoundTag getTag() {
        return this.tag;
    }

    @Override
    public boolean isSkippable() {
        return true;
    }
}
package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundLevelEventPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundLevelEventPacket> f_315017_ = Packet.m_319422_(
        ClientboundLevelEventPacket::m_132275_, ClientboundLevelEventPacket::new
    );
    private final int type;
    private final BlockPos pos;
    private final int data;
    private final boolean globalEvent;

    public ClientboundLevelEventPacket(int pType, BlockPos pPos, int pData, boolean pGlobalEvent) {
        this.type = pType;
        this.pos = pPos.immutable();
        this.data = pData;
        this.globalEvent = pGlobalEvent;
    }

    private ClientboundLevelEventPacket(FriendlyByteBuf pBuffer) {
        this.type = pBuffer.readInt();
        this.pos = pBuffer.readBlockPos();
        this.data = pBuffer.readInt();
        this.globalEvent = pBuffer.readBoolean();
    }

    private void m_132275_(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(this.type);
        pBuffer.writeBlockPos(this.pos);
        pBuffer.writeInt(this.data);
        pBuffer.writeBoolean(this.globalEvent);
    }

    @Override
    public PacketType<ClientboundLevelEventPacket> write() {
        return GamePacketTypes.f_314846_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleLevelEvent(this);
    }

    public boolean isGlobalEvent() {
        return this.globalEvent;
    }

    public int getType() {
        return this.type;
    }

    public int getData() {
        return this.data;
    }

    public BlockPos getPos() {
        return this.pos;
    }
}
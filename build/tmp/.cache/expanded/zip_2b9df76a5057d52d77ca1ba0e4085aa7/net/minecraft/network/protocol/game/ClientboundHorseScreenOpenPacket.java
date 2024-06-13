package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundHorseScreenOpenPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundHorseScreenOpenPacket> f_314879_ = Packet.m_319422_(
        ClientboundHorseScreenOpenPacket::m_132205_, ClientboundHorseScreenOpenPacket::new
    );
    private final int containerId;
    private final int size;
    private final int entityId;

    public ClientboundHorseScreenOpenPacket(int pContainerId, int pSize, int pEntityId) {
        this.containerId = pContainerId;
        this.size = pSize;
        this.entityId = pEntityId;
    }

    private ClientboundHorseScreenOpenPacket(FriendlyByteBuf pBuffer) {
        this.containerId = pBuffer.readUnsignedByte();
        this.size = pBuffer.readVarInt();
        this.entityId = pBuffer.readInt();
    }

    private void m_132205_(FriendlyByteBuf pBuffer) {
        pBuffer.writeByte(this.containerId);
        pBuffer.writeVarInt(this.size);
        pBuffer.writeInt(this.entityId);
    }

    @Override
    public PacketType<ClientboundHorseScreenOpenPacket> write() {
        return GamePacketTypes.f_314122_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleHorseScreenOpen(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public int getSize() {
        return this.size;
    }

    public int getEntityId() {
        return this.entityId;
    }
}
package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;

public class ClientboundSetEntityLinkPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundSetEntityLinkPacket> f_314212_ = Packet.m_319422_(
        ClientboundSetEntityLinkPacket::m_133173_, ClientboundSetEntityLinkPacket::new
    );
    private final int sourceId;
    private final int destId;

    public ClientboundSetEntityLinkPacket(Entity pSource, @Nullable Entity pDestination) {
        this.sourceId = pSource.getId();
        this.destId = pDestination != null ? pDestination.getId() : 0;
    }

    private ClientboundSetEntityLinkPacket(FriendlyByteBuf pBuffer) {
        this.sourceId = pBuffer.readInt();
        this.destId = pBuffer.readInt();
    }

    private void m_133173_(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(this.sourceId);
        pBuffer.writeInt(this.destId);
    }

    @Override
    public PacketType<ClientboundSetEntityLinkPacket> write() {
        return GamePacketTypes.f_314312_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleEntityLinkPacket(this);
    }

    public int getSourceId() {
        return this.sourceId;
    }

    public int getDestId() {
        return this.destId;
    }
}
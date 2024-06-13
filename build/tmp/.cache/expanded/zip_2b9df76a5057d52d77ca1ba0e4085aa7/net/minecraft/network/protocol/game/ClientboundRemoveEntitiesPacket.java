package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundRemoveEntitiesPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundRemoveEntitiesPacket> f_315069_ = Packet.m_319422_(
        ClientboundRemoveEntitiesPacket::m_182724_, ClientboundRemoveEntitiesPacket::new
    );
    private final IntList entityIds;

    public ClientboundRemoveEntitiesPacket(IntList pEntityIds) {
        this.entityIds = new IntArrayList(pEntityIds);
    }

    public ClientboundRemoveEntitiesPacket(int... pEntityIds) {
        this.entityIds = new IntArrayList(pEntityIds);
    }

    private ClientboundRemoveEntitiesPacket(FriendlyByteBuf pBuffer) {
        this.entityIds = pBuffer.readIntIdList();
    }

    private void m_182724_(FriendlyByteBuf pBuffer) {
        pBuffer.writeIntIdList(this.entityIds);
    }

    @Override
    public PacketType<ClientboundRemoveEntitiesPacket> write() {
        return GamePacketTypes.f_315935_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleRemoveEntities(this);
    }

    public IntList getEntityIds() {
        return this.entityIds;
    }
}
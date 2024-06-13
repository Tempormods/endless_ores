package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundSetSimulationDistancePacket(int simulationDistance) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundSetSimulationDistancePacket> f_314799_ = Packet.m_319422_(
        ClientboundSetSimulationDistancePacket::m_195801_, ClientboundSetSimulationDistancePacket::new
    );

    private ClientboundSetSimulationDistancePacket(FriendlyByteBuf pBuffer) {
        this(pBuffer.readVarInt());
    }

    private void m_195801_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.simulationDistance);
    }

    @Override
    public PacketType<ClientboundSetSimulationDistancePacket> write() {
        return GamePacketTypes.f_314752_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleSetSimulationDistance(this);
    }
}
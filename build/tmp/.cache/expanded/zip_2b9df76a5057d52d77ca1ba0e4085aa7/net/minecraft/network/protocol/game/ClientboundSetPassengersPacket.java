package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;

public class ClientboundSetPassengersPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundSetPassengersPacket> f_316682_ = Packet.m_319422_(
        ClientboundSetPassengersPacket::m_133284_, ClientboundSetPassengersPacket::new
    );
    private final int vehicle;
    private final int[] passengers;

    public ClientboundSetPassengersPacket(Entity pVehicle) {
        this.vehicle = pVehicle.getId();
        List<Entity> list = pVehicle.getPassengers();
        this.passengers = new int[list.size()];

        for (int i = 0; i < list.size(); i++) {
            this.passengers[i] = list.get(i).getId();
        }
    }

    private ClientboundSetPassengersPacket(FriendlyByteBuf pBuffer) {
        this.vehicle = pBuffer.readVarInt();
        this.passengers = pBuffer.readVarIntArray();
    }

    private void m_133284_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.vehicle);
        pBuffer.writeVarIntArray(this.passengers);
    }

    @Override
    public PacketType<ClientboundSetPassengersPacket> write() {
        return GamePacketTypes.f_314908_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleSetEntityPassengersPacket(this);
    }

    public int[] getPassengers() {
        return this.passengers;
    }

    public int getVehicle() {
        return this.vehicle;
    }
}
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundSetHealthPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundSetHealthPacket> f_315208_ = Packet.m_319422_(
        ClientboundSetHealthPacket::m_133248_, ClientboundSetHealthPacket::new
    );
    private final float health;
    private final int food;
    private final float saturation;

    public ClientboundSetHealthPacket(float pHealth, int pFood, float pSaturation) {
        this.health = pHealth;
        this.food = pFood;
        this.saturation = pSaturation;
    }

    private ClientboundSetHealthPacket(FriendlyByteBuf pBuffer) {
        this.health = pBuffer.readFloat();
        this.food = pBuffer.readVarInt();
        this.saturation = pBuffer.readFloat();
    }

    private void m_133248_(FriendlyByteBuf pBuffer) {
        pBuffer.writeFloat(this.health);
        pBuffer.writeVarInt(this.food);
        pBuffer.writeFloat(this.saturation);
    }

    @Override
    public PacketType<ClientboundSetHealthPacket> write() {
        return GamePacketTypes.f_315504_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleSetHealth(this);
    }

    public float getHealth() {
        return this.health;
    }

    public int getFood() {
        return this.food;
    }

    public float getSaturation() {
        return this.saturation;
    }
}
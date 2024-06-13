package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundStartConfigurationPacket implements Packet<ClientGamePacketListener> {
    public static final ClientboundStartConfigurationPacket f_316636_ = new ClientboundStartConfigurationPacket();
    public static final StreamCodec<ByteBuf, ClientboundStartConfigurationPacket> f_314184_ = StreamCodec.m_323136_(f_316636_);

    private ClientboundStartConfigurationPacket() {
    }

    @Override
    public PacketType<ClientboundStartConfigurationPacket> write() {
        return GamePacketTypes.f_315258_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleConfigurationStart(this);
    }

    @Override
    public boolean m_319635_() {
        return true;
    }
}
package net.minecraft.network.protocol.configuration;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundFinishConfigurationPacket implements Packet<ClientConfigurationPacketListener> {
    public static final ClientboundFinishConfigurationPacket f_315319_ = new ClientboundFinishConfigurationPacket();
    public static final StreamCodec<ByteBuf, ClientboundFinishConfigurationPacket> f_314975_ = StreamCodec.m_323136_(f_315319_);

    private ClientboundFinishConfigurationPacket() {
    }

    @Override
    public PacketType<ClientboundFinishConfigurationPacket> write() {
        return ConfigurationPacketTypes.f_315770_;
    }

    public void handle(ClientConfigurationPacketListener pHandler) {
        pHandler.handleConfigurationFinished(this);
    }

    @Override
    public boolean m_319635_() {
        return true;
    }
}
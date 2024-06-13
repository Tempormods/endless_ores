package net.minecraft.network.protocol.configuration;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundResetChatPacket implements Packet<ClientConfigurationPacketListener> {
    public static final ClientboundResetChatPacket f_314686_ = new ClientboundResetChatPacket();
    public static final StreamCodec<ByteBuf, ClientboundResetChatPacket> f_314414_ = StreamCodec.m_323136_(f_314686_);

    private ClientboundResetChatPacket() {
    }

    @Override
    public PacketType<ClientboundResetChatPacket> write() {
        return ConfigurationPacketTypes.f_315012_;
    }

    public void handle(ClientConfigurationPacketListener p_328763_) {
        p_328763_.m_318905_(this);
    }
}
package net.minecraft.network.protocol.configuration;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundFinishConfigurationPacket implements Packet<ServerConfigurationPacketListener> {
    public static final ServerboundFinishConfigurationPacket f_315825_ = new ServerboundFinishConfigurationPacket();
    public static final StreamCodec<ByteBuf, ServerboundFinishConfigurationPacket> f_315521_ = StreamCodec.m_323136_(f_315825_);

    private ServerboundFinishConfigurationPacket() {
    }

    @Override
    public PacketType<ServerboundFinishConfigurationPacket> write() {
        return ConfigurationPacketTypes.f_314238_;
    }

    public void handle(ServerConfigurationPacketListener p_299852_) {
        p_299852_.handleConfigurationFinished(this);
    }

    @Override
    public boolean m_319635_() {
        return true;
    }
}
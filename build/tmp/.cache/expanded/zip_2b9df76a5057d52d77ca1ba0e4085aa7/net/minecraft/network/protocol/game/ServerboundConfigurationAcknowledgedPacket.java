package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundConfigurationAcknowledgedPacket implements Packet<ServerGamePacketListener> {
    public static final ServerboundConfigurationAcknowledgedPacket f_317101_ = new ServerboundConfigurationAcknowledgedPacket();
    public static final StreamCodec<ByteBuf, ServerboundConfigurationAcknowledgedPacket> f_314723_ = StreamCodec.m_323136_(f_317101_);

    private ServerboundConfigurationAcknowledgedPacket() {
    }

    @Override
    public PacketType<ServerboundConfigurationAcknowledgedPacket> write() {
        return GamePacketTypes.f_315986_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleConfigurationAcknowledged(this);
    }

    @Override
    public boolean m_319635_() {
        return true;
    }
}
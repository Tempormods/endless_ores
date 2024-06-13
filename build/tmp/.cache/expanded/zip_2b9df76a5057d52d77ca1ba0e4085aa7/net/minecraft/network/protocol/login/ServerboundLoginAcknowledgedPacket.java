package net.minecraft.network.protocol.login;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundLoginAcknowledgedPacket implements Packet<ServerLoginPacketListener> {
    public static final ServerboundLoginAcknowledgedPacket f_314965_ = new ServerboundLoginAcknowledgedPacket();
    public static final StreamCodec<ByteBuf, ServerboundLoginAcknowledgedPacket> f_314115_ = StreamCodec.m_323136_(f_314965_);

    private ServerboundLoginAcknowledgedPacket() {
    }

    @Override
    public PacketType<ServerboundLoginAcknowledgedPacket> write() {
        return LoginPacketTypes.f_314277_;
    }

    public void handle(ServerLoginPacketListener pHandler) {
        pHandler.handleLoginAcknowledgement(this);
    }

    @Override
    public boolean m_319635_() {
        return true;
    }
}
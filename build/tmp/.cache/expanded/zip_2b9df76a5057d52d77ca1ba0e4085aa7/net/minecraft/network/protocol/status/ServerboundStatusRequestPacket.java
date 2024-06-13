package net.minecraft.network.protocol.status;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundStatusRequestPacket implements Packet<ServerStatusPacketListener> {
    public static final ServerboundStatusRequestPacket f_315857_ = new ServerboundStatusRequestPacket();
    public static final StreamCodec<ByteBuf, ServerboundStatusRequestPacket> f_314883_ = StreamCodec.m_323136_(f_315857_);

    private ServerboundStatusRequestPacket() {
    }

    @Override
    public PacketType<ServerboundStatusRequestPacket> write() {
        return StatusPacketTypes.f_316223_;
    }

    public void handle(ServerStatusPacketListener pHandler) {
        pHandler.handleStatusRequest(this);
    }
}
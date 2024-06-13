package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundChunkBatchStartPacket implements Packet<ClientGamePacketListener> {
    public static final ClientboundChunkBatchStartPacket f_314811_ = new ClientboundChunkBatchStartPacket();
    public static final StreamCodec<ByteBuf, ClientboundChunkBatchStartPacket> f_316871_ = StreamCodec.m_323136_(f_314811_);

    private ClientboundChunkBatchStartPacket() {
    }

    @Override
    public PacketType<ClientboundChunkBatchStartPacket> write() {
        return GamePacketTypes.f_316085_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleChunkBatchStart(this);
    }
}
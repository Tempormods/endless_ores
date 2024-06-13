package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundSetDefaultSpawnPositionPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundSetDefaultSpawnPositionPacket> f_314422_ = Packet.m_319422_(
        ClientboundSetDefaultSpawnPositionPacket::m_133124_, ClientboundSetDefaultSpawnPositionPacket::new
    );
    private final BlockPos pos;
    private final float angle;

    public ClientboundSetDefaultSpawnPositionPacket(BlockPos pPos, float pAngle) {
        this.pos = pPos;
        this.angle = pAngle;
    }

    private ClientboundSetDefaultSpawnPositionPacket(FriendlyByteBuf pBuffer) {
        this.pos = pBuffer.readBlockPos();
        this.angle = pBuffer.readFloat();
    }

    private void m_133124_(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(this.pos);
        pBuffer.writeFloat(this.angle);
    }

    @Override
    public PacketType<ClientboundSetDefaultSpawnPositionPacket> write() {
        return GamePacketTypes.f_315211_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleSetSpawn(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public float getAngle() {
        return this.angle;
    }
}
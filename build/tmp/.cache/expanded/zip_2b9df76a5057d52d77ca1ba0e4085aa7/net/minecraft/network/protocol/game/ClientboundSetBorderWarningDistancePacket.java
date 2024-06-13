package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderWarningDistancePacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundSetBorderWarningDistancePacket> f_316932_ = Packet.m_319422_(
        ClientboundSetBorderWarningDistancePacket::m_179270_, ClientboundSetBorderWarningDistancePacket::new
    );
    private final int warningBlocks;

    public ClientboundSetBorderWarningDistancePacket(WorldBorder pWorldBorder) {
        this.warningBlocks = pWorldBorder.getWarningBlocks();
    }

    private ClientboundSetBorderWarningDistancePacket(FriendlyByteBuf pBuffer) {
        this.warningBlocks = pBuffer.readVarInt();
    }

    private void m_179270_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.warningBlocks);
    }

    @Override
    public PacketType<ClientboundSetBorderWarningDistancePacket> write() {
        return GamePacketTypes.f_314899_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleSetBorderWarningDistance(this);
    }

    public int getWarningBlocks() {
        return this.warningBlocks;
    }
}
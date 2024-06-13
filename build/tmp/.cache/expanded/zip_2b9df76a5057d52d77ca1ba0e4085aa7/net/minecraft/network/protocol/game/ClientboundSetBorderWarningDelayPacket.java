package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderWarningDelayPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundSetBorderWarningDelayPacket> f_316106_ = Packet.m_319422_(
        ClientboundSetBorderWarningDelayPacket::m_179258_, ClientboundSetBorderWarningDelayPacket::new
    );
    private final int warningDelay;

    public ClientboundSetBorderWarningDelayPacket(WorldBorder pWorldBorder) {
        this.warningDelay = pWorldBorder.getWarningTime();
    }

    private ClientboundSetBorderWarningDelayPacket(FriendlyByteBuf pBuffer) {
        this.warningDelay = pBuffer.readVarInt();
    }

    private void m_179258_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.warningDelay);
    }

    @Override
    public PacketType<ClientboundSetBorderWarningDelayPacket> write() {
        return GamePacketTypes.f_315324_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleSetBorderWarningDelay(this);
    }

    public int getWarningDelay() {
        return this.warningDelay;
    }
}
package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundClearTitlesPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundClearTitlesPacket> f_314613_ = Packet.m_319422_(
        ClientboundClearTitlesPacket::m_178782_, ClientboundClearTitlesPacket::new
    );
    private final boolean resetTimes;

    public ClientboundClearTitlesPacket(boolean pResetTimes) {
        this.resetTimes = pResetTimes;
    }

    private ClientboundClearTitlesPacket(FriendlyByteBuf pBuffer) {
        this.resetTimes = pBuffer.readBoolean();
    }

    private void m_178782_(FriendlyByteBuf pBuffer) {
        pBuffer.writeBoolean(this.resetTimes);
    }

    @Override
    public PacketType<ClientboundClearTitlesPacket> write() {
        return GamePacketTypes.f_314083_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleTitlesClear(this);
    }

    public boolean shouldResetTimes() {
        return this.resetTimes;
    }
}
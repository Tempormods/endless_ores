package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundSetTimePacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundSetTimePacket> f_315948_ = Packet.m_319422_(
        ClientboundSetTimePacket::m_133359_, ClientboundSetTimePacket::new
    );
    private final long gameTime;
    private final long dayTime;

    public ClientboundSetTimePacket(long pGameTime, long pDayTime, boolean pDaylightCycleEnabled) {
        this.gameTime = pGameTime;
        long i = pDayTime;
        if (!pDaylightCycleEnabled) {
            i = -pDayTime;
            if (i == 0L) {
                i = -1L;
            }
        }

        this.dayTime = i;
    }

    private ClientboundSetTimePacket(FriendlyByteBuf pBuffer) {
        this.gameTime = pBuffer.readLong();
        this.dayTime = pBuffer.readLong();
    }

    private void m_133359_(FriendlyByteBuf pBuffer) {
        pBuffer.writeLong(this.gameTime);
        pBuffer.writeLong(this.dayTime);
    }

    @Override
    public PacketType<ClientboundSetTimePacket> write() {
        return GamePacketTypes.f_316320_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleSetTime(this);
    }

    public long getGameTime() {
        return this.gameTime;
    }

    public long getDayTime() {
        return this.dayTime;
    }
}
package net.minecraft.network.protocol.common;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundResourcePackPacket(UUID f_302393_, ServerboundResourcePackPacket.Action action) implements Packet<ServerCommonPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundResourcePackPacket> f_316982_ = Packet.m_319422_(
        ServerboundResourcePackPacket::m_292879_, ServerboundResourcePackPacket::new
    );

    private ServerboundResourcePackPacket(FriendlyByteBuf pBuffer) {
        this(pBuffer.readUUID(), pBuffer.readEnum(ServerboundResourcePackPacket.Action.class));
    }

    private void m_292879_(FriendlyByteBuf pBuffer) {
        pBuffer.writeUUID(this.f_302393_);
        pBuffer.writeEnum(this.action);
    }

    @Override
    public PacketType<ServerboundResourcePackPacket> write() {
        return CommonPacketTypes.f_316458_;
    }

    public void handle(ServerCommonPacketListener pHandler) {
        pHandler.handleResourcePackResponse(this);
    }

    public static enum Action {
        SUCCESSFULLY_LOADED,
        DECLINED,
        FAILED_DOWNLOAD,
        ACCEPTED,
        DOWNLOADED,
        INVALID_URL,
        FAILED_RELOAD,
        DISCARDED;

        public boolean m_307084_() {
            return this != ACCEPTED && this != DOWNLOADED;
        }
    }
}
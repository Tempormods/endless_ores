package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundClientCommandPacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundClientCommandPacket> f_315392_ = Packet.m_319422_(
        ServerboundClientCommandPacket::m_133851_, ServerboundClientCommandPacket::new
    );
    private final ServerboundClientCommandPacket.Action action;

    public ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action pAction) {
        this.action = pAction;
    }

    private ServerboundClientCommandPacket(FriendlyByteBuf pBuffer) {
        this.action = pBuffer.readEnum(ServerboundClientCommandPacket.Action.class);
    }

    private void m_133851_(FriendlyByteBuf pBuffer) {
        pBuffer.writeEnum(this.action);
    }

    @Override
    public PacketType<ServerboundClientCommandPacket> write() {
        return GamePacketTypes.f_314416_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleClientCommand(this);
    }

    public ServerboundClientCommandPacket.Action getAction() {
        return this.action;
    }

    public static enum Action {
        PERFORM_RESPAWN,
        REQUEST_STATS;
    }
}
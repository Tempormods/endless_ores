package net.minecraft.network.protocol.login;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.login.custom.CustomQueryAnswerPayload;
import net.minecraft.network.protocol.login.custom.DiscardedQueryAnswerPayload;

public record ServerboundCustomQueryAnswerPacket(int transactionId, @Nullable CustomQueryAnswerPayload payload) implements Packet<ServerLoginPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundCustomQueryAnswerPacket> f_315594_ = Packet.m_319422_(
        ServerboundCustomQueryAnswerPacket::m_292944_, ServerboundCustomQueryAnswerPacket::read
    );
    private static final int MAX_PAYLOAD_SIZE = 1048576;

    private static ServerboundCustomQueryAnswerPacket read(FriendlyByteBuf pBuffer) {
        int i = pBuffer.readVarInt();
        return new ServerboundCustomQueryAnswerPacket(i, readPayload(i, pBuffer));
    }

    private static CustomQueryAnswerPayload readPayload(int pTransactionId, FriendlyByteBuf pBuffer) {
        return readUnknownPayload(pBuffer);
    }

    private static CustomQueryAnswerPayload readUnknownPayload(FriendlyByteBuf pBuffer) {
        int i = pBuffer.readableBytes();
        if (i >= 0 && i <= 1048576) {
            pBuffer.skipBytes(i);
            return DiscardedQueryAnswerPayload.INSTANCE;
        } else {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }

    private void m_292944_(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.transactionId);
        pBuffer.m_321806_(this.payload, (p_300758_, p_298999_) -> p_298999_.write(p_300758_));
    }

    @Override
    public PacketType<ServerboundCustomQueryAnswerPacket> write() {
        return LoginPacketTypes.f_316432_;
    }

    public void handle(ServerLoginPacketListener pHandler) {
        pHandler.handleCustomQueryPacket(this);
    }
}
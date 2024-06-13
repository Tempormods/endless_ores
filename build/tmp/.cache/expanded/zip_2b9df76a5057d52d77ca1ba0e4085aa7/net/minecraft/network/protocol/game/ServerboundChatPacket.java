package net.minecraft.network.protocol.game;

import java.time.Instant;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundChatPacket(String message, Instant timeStamp, long salt, @Nullable MessageSignature signature, LastSeenMessages.Update lastSeenMessages)
    implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundChatPacket> f_315924_ = Packet.m_319422_(
        ServerboundChatPacket::m_133838_, ServerboundChatPacket::new
    );

    private ServerboundChatPacket(FriendlyByteBuf pBuffer) {
        this(
            pBuffer.readUtf(256),
            pBuffer.readInstant(),
            pBuffer.readLong(),
            pBuffer.readNullable(MessageSignature::read),
            new LastSeenMessages.Update(pBuffer)
        );
    }

    private void m_133838_(FriendlyByteBuf pBuffer) {
        pBuffer.writeUtf(this.message, 256);
        pBuffer.writeInstant(this.timeStamp);
        pBuffer.writeLong(this.salt);
        pBuffer.m_321806_(this.signature, MessageSignature::write);
        this.lastSeenMessages.write(pBuffer);
    }

    @Override
    public PacketType<ServerboundChatPacket> write() {
        return GamePacketTypes.f_315716_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleChat(this);
    }
}
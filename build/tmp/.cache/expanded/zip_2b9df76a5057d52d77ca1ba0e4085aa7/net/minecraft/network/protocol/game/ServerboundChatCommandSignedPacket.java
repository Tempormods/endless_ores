package net.minecraft.network.protocol.game;

import java.time.Instant;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundChatCommandSignedPacket(
    String f_315558_, Instant f_315619_, long f_315485_, ArgumentSignatures f_314857_, LastSeenMessages.Update f_316504_
) implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundChatCommandSignedPacket> f_313935_ = Packet.m_319422_(
        ServerboundChatCommandSignedPacket::m_321970_, ServerboundChatCommandSignedPacket::new
    );

    private ServerboundChatCommandSignedPacket(FriendlyByteBuf p_333361_) {
        this(p_333361_.readUtf(), p_333361_.readInstant(), p_333361_.readLong(), new ArgumentSignatures(p_333361_), new LastSeenMessages.Update(p_333361_));
    }

    private void m_321970_(FriendlyByteBuf p_332640_) {
        p_332640_.writeUtf(this.f_315558_);
        p_332640_.writeInstant(this.f_315619_);
        p_332640_.writeLong(this.f_315485_);
        this.f_314857_.write(p_332640_);
        this.f_316504_.write(p_332640_);
    }

    @Override
    public PacketType<ServerboundChatCommandSignedPacket> write() {
        return GamePacketTypes.f_315616_;
    }

    public void handle(ServerGamePacketListener p_329693_) {
        p_329693_.m_321262_(this);
    }
}
package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundCustomChatCompletionsPacket(ClientboundCustomChatCompletionsPacket.Action action, List<String> entries)
    implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundCustomChatCompletionsPacket> f_316575_ = Packet.m_319422_(
        ClientboundCustomChatCompletionsPacket::m_240693_, ClientboundCustomChatCompletionsPacket::new
    );

    private ClientboundCustomChatCompletionsPacket(FriendlyByteBuf pBuffer) {
        this(pBuffer.readEnum(ClientboundCustomChatCompletionsPacket.Action.class), pBuffer.readList(FriendlyByteBuf::readUtf));
    }

    private void m_240693_(FriendlyByteBuf pBuffer) {
        pBuffer.writeEnum(this.action);
        pBuffer.writeCollection(this.entries, FriendlyByteBuf::writeUtf);
    }

    @Override
    public PacketType<ClientboundCustomChatCompletionsPacket> write() {
        return GamePacketTypes.f_316409_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleCustomChatCompletions(this);
    }

    public static enum Action {
        ADD,
        REMOVE,
        SET;
    }
}
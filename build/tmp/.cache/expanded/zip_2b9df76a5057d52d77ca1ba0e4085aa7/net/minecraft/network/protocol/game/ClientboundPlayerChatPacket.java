package net.minecraft.network.protocol.game;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundPlayerChatPacket(
    UUID sender,
    int index,
    @Nullable MessageSignature signature,
    SignedMessageBody.Packed body,
    @Nullable Component unsignedContent,
    FilterMask filterMask,
    ChatType.Bound chatType
) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPlayerChatPacket> f_314263_ = Packet.m_319422_(
        ClientboundPlayerChatPacket::m_237754_, ClientboundPlayerChatPacket::new
    );

    private ClientboundPlayerChatPacket(RegistryFriendlyByteBuf p_329037_) {
        this(
            p_329037_.readUUID(),
            p_329037_.readVarInt(),
            p_329037_.readNullable(MessageSignature::read),
            new SignedMessageBody.Packed(p_329037_),
            FriendlyByteBuf.m_323524_(p_329037_, ComponentSerialization.f_316335_),
            FilterMask.read(p_329037_),
            ChatType.Bound.f_316893_.m_318688_(p_329037_)
        );
    }

    private void m_237754_(RegistryFriendlyByteBuf p_329687_) {
        p_329687_.writeUUID(this.sender);
        p_329687_.writeVarInt(this.index);
        p_329687_.m_321806_(this.signature, MessageSignature::write);
        this.body.write(p_329687_);
        FriendlyByteBuf.writeNullable(p_329687_, this.unsignedContent, ComponentSerialization.f_316335_);
        FilterMask.write(p_329687_, this.filterMask);
        ChatType.Bound.f_316893_.m_318638_(p_329687_, this.chatType);
    }

    @Override
    public PacketType<ClientboundPlayerChatPacket> write() {
        return GamePacketTypes.f_315278_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handlePlayerChat(this);
    }

    @Override
    public boolean isSkippable() {
        return true;
    }
}
package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundTransferPacket(String f_314661_, int f_316558_) implements Packet<ClientCommonPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundTransferPacket> f_316509_ = Packet.m_319422_(
        ClientboundTransferPacket::m_318722_, ClientboundTransferPacket::new
    );

    private ClientboundTransferPacket(FriendlyByteBuf p_330783_) {
        this(p_330783_.readUtf(), p_330783_.readVarInt());
    }

    private void m_318722_(FriendlyByteBuf p_329224_) {
        p_329224_.writeUtf(this.f_314661_);
        p_329224_.writeVarInt(this.f_316558_);
    }

    @Override
    public PacketType<ClientboundTransferPacket> write() {
        return CommonPacketTypes.f_316077_;
    }

    public void handle(ClientCommonPacketListener p_328535_) {
        p_328535_.m_319408_(this);
    }
}
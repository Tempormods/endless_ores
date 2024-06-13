package net.minecraft.network.protocol.common;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundResourcePackPopPacket(Optional<UUID> f_302279_) implements Packet<ClientCommonPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundResourcePackPopPacket> f_314321_ = Packet.m_319422_(
        ClientboundResourcePackPopPacket::m_307616_, ClientboundResourcePackPopPacket::new
    );

    private ClientboundResourcePackPopPacket(FriendlyByteBuf p_310483_) {
        this(p_310483_.readOptional(UUIDUtil.f_315346_));
    }

    private void m_307616_(FriendlyByteBuf p_311086_) {
        p_311086_.writeOptional(this.f_302279_, UUIDUtil.f_315346_);
    }

    @Override
    public PacketType<ClientboundResourcePackPopPacket> write() {
        return CommonPacketTypes.f_315299_;
    }

    public void handle(ClientCommonPacketListener p_311428_) {
        p_311428_.handleResourcePack(this);
    }
}
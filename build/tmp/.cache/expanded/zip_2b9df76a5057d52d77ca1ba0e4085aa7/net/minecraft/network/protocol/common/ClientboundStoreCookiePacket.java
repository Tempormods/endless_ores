package net.minecraft.network.protocol.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public record ClientboundStoreCookiePacket(ResourceLocation f_314603_, byte[] f_314170_) implements Packet<ClientCommonPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundStoreCookiePacket> f_313911_ = Packet.m_319422_(
        ClientboundStoreCookiePacket::m_319185_, ClientboundStoreCookiePacket::new
    );
    private static final int f_316243_ = 5120;
    public static final StreamCodec<ByteBuf, byte[]> f_315070_ = ByteBufCodecs.m_323478_(5120);

    private ClientboundStoreCookiePacket(FriendlyByteBuf p_331845_) {
        this(p_331845_.readResourceLocation(), f_315070_.m_318688_(p_331845_));
    }

    private void m_319185_(FriendlyByteBuf p_330443_) {
        p_330443_.writeResourceLocation(this.f_314603_);
        f_315070_.m_318638_(p_330443_, this.f_314170_);
    }

    @Override
    public PacketType<ClientboundStoreCookiePacket> write() {
        return CommonPacketTypes.f_313908_;
    }

    public void handle(ClientCommonPacketListener p_334968_) {
        p_334968_.m_320373_(this);
    }
}
package net.minecraft.network.protocol.cookie;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.ClientboundStoreCookiePacket;
import net.minecraft.resources.ResourceLocation;

public record ServerboundCookieResponsePacket(ResourceLocation f_314961_, @Nullable byte[] f_315738_) implements Packet<ServerCookiePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundCookieResponsePacket> f_316817_ = Packet.m_319422_(
        ServerboundCookieResponsePacket::m_319272_, ServerboundCookieResponsePacket::new
    );

    private ServerboundCookieResponsePacket(FriendlyByteBuf p_335580_) {
        this(p_335580_.readResourceLocation(), p_335580_.readNullable(ClientboundStoreCookiePacket.f_315070_));
    }

    private void m_319272_(FriendlyByteBuf p_329068_) {
        p_329068_.writeResourceLocation(this.f_314961_);
        p_329068_.m_321806_(this.f_315738_, ClientboundStoreCookiePacket.f_315070_);
    }

    @Override
    public PacketType<ServerboundCookieResponsePacket> write() {
        return CookiePacketTypes.f_316493_;
    }

    public void handle(ServerCookiePacketListener p_329041_) {
        p_329041_.m_320234_(this);
    }
}
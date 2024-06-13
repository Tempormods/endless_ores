package net.minecraft.network.protocol.cookie;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public record ClientboundCookieRequestPacket(ResourceLocation f_315050_) implements Packet<ClientCookiePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundCookieRequestPacket> f_314850_ = Packet.m_319422_(
        ClientboundCookieRequestPacket::m_321683_, ClientboundCookieRequestPacket::new
    );

    private ClientboundCookieRequestPacket(FriendlyByteBuf p_331420_) {
        this(p_331420_.readResourceLocation());
    }

    private void m_321683_(FriendlyByteBuf p_330468_) {
        p_330468_.writeResourceLocation(this.f_315050_);
    }

    @Override
    public PacketType<ClientboundCookieRequestPacket> write() {
        return CookiePacketTypes.f_314706_;
    }

    public void handle(ClientCookiePacketListener p_335745_) {
        p_335745_.m_320309_(this);
    }
}
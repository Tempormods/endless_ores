package net.minecraft.network.protocol.common;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundResourcePackPushPacket(UUID f_303323_, String f_302762_, String f_302355_, boolean f_302925_, Optional<Component> f_303164_)
    implements Packet<ClientCommonPacketListener> {
    public static final int f_302560_ = 40;
    public static final StreamCodec<ByteBuf, ClientboundResourcePackPushPacket> f_314484_ = StreamCodec.m_319894_(
        UUIDUtil.f_315346_,
        ClientboundResourcePackPushPacket::f_303323_,
        ByteBufCodecs.f_315450_,
        ClientboundResourcePackPushPacket::f_302762_,
        ByteBufCodecs.m_319534_(40),
        ClientboundResourcePackPushPacket::f_302355_,
        ByteBufCodecs.f_315514_,
        ClientboundResourcePackPushPacket::f_302925_,
        ComponentSerialization.f_314039_.m_321801_(ByteBufCodecs::m_319027_),
        ClientboundResourcePackPushPacket::f_303164_,
        ClientboundResourcePackPushPacket::new
    );

    public ClientboundResourcePackPushPacket(UUID f_303323_, String f_302762_, String f_302355_, boolean f_302925_, Optional<Component> f_303164_) {
        if (f_302355_.length() > 40) {
            throw new IllegalArgumentException("Hash is too long (max 40, was " + f_302355_.length() + ")");
        } else {
            this.f_303323_ = f_303323_;
            this.f_302762_ = f_302762_;
            this.f_302355_ = f_302355_;
            this.f_302925_ = f_302925_;
            this.f_303164_ = f_303164_;
        }
    }

    @Override
    public PacketType<ClientboundResourcePackPushPacket> write() {
        return CommonPacketTypes.f_316687_;
    }

    public void handle(ClientCommonPacketListener p_312649_) {
        p_312649_.m_305543_(this);
    }
}
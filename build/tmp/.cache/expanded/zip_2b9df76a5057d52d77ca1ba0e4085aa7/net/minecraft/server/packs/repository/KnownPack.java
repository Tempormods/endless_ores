package net.minecraft.server.packs.repository;

import io.netty.buffer.ByteBuf;
import net.minecraft.SharedConstants;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record KnownPack(String f_315458_, String f_315279_, String f_314534_) {
    public static final StreamCodec<ByteBuf, KnownPack> f_315534_ = StreamCodec.m_321516_(
        ByteBufCodecs.f_315450_,
        KnownPack::f_315458_,
        ByteBufCodecs.f_315450_,
        KnownPack::f_315279_,
        ByteBufCodecs.f_315450_,
        KnownPack::f_314534_,
        KnownPack::new
    );
    public static final String f_313921_ = "minecraft";

    public static KnownPack m_321609_(String p_332942_) {
        return new KnownPack("minecraft", p_332942_, SharedConstants.getCurrentVersion().getId());
    }

    public boolean m_323138_() {
        return this.f_315458_.equals("minecraft");
    }

    @Override
    public String toString() {
        return this.f_315458_ + ":" + this.f_315279_ + ":" + this.f_314534_;
    }
}
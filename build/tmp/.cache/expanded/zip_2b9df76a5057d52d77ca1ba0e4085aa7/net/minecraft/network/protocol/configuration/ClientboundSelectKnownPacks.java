package net.minecraft.network.protocol.configuration;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.packs.repository.KnownPack;

public record ClientboundSelectKnownPacks(List<KnownPack> f_315150_) implements Packet<ClientConfigurationPacketListener> {
    public static final StreamCodec<ByteBuf, ClientboundSelectKnownPacks> f_316990_ = StreamCodec.m_322204_(
        KnownPack.f_315534_.m_321801_(ByteBufCodecs.m_324765_()), ClientboundSelectKnownPacks::f_315150_, ClientboundSelectKnownPacks::new
    );

    @Override
    public PacketType<ClientboundSelectKnownPacks> write() {
        return ConfigurationPacketTypes.f_316719_;
    }

    public void handle(ClientConfigurationPacketListener p_331655_) {
        p_331655_.m_319752_(this);
    }
}
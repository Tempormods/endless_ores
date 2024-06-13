package net.minecraft.network.protocol.configuration;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.packs.repository.KnownPack;

public record ServerboundSelectKnownPacks(List<KnownPack> f_315709_) implements Packet<ServerConfigurationPacketListener> {
    public static final StreamCodec<ByteBuf, ServerboundSelectKnownPacks> f_314730_ = StreamCodec.m_322204_(
        KnownPack.f_315534_.m_321801_(ByteBufCodecs.m_319259_(64)), ServerboundSelectKnownPacks::f_315709_, ServerboundSelectKnownPacks::new
    );

    @Override
    public PacketType<ServerboundSelectKnownPacks> write() {
        return ConfigurationPacketTypes.f_315102_;
    }

    public void handle(ServerConfigurationPacketListener p_331579_) {
        p_331579_.m_322556_(this);
    }
}
package net.minecraft.network.protocol.configuration;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record ClientboundRegistryDataPacket(ResourceKey<? extends Registry<?>> f_316367_, List<RegistrySynchronization.PackedRegistryEntry> f_315072_)
    implements Packet<ClientConfigurationPacketListener> {
    private static final StreamCodec<ByteBuf, ResourceKey<? extends Registry<?>>> f_315798_ = ResourceLocation.f_314488_
        .m_323038_(ResourceKey::createRegistryKey, ResourceKey::location);
    public static final StreamCodec<FriendlyByteBuf, ClientboundRegistryDataPacket> f_316830_ = StreamCodec.m_320349_(
        f_315798_,
        ClientboundRegistryDataPacket::f_316367_,
        RegistrySynchronization.PackedRegistryEntry.f_316015_.m_321801_(ByteBufCodecs.m_324765_()),
        ClientboundRegistryDataPacket::f_315072_,
        ClientboundRegistryDataPacket::new
    );

    @Override
    public PacketType<ClientboundRegistryDataPacket> write() {
        return ConfigurationPacketTypes.f_314997_;
    }

    public void handle(ClientConfigurationPacketListener pHandler) {
        pHandler.handleRegistryData(this);
    }
}
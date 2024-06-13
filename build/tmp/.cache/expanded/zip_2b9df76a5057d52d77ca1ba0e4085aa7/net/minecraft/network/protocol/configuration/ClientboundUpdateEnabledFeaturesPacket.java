package net.minecraft.network.protocol.configuration;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public record ClientboundUpdateEnabledFeaturesPacket(Set<ResourceLocation> features) implements Packet<ClientConfigurationPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundUpdateEnabledFeaturesPacket> f_314036_ = Packet.m_319422_(
        ClientboundUpdateEnabledFeaturesPacket::m_294838_, ClientboundUpdateEnabledFeaturesPacket::new
    );

    private ClientboundUpdateEnabledFeaturesPacket(FriendlyByteBuf pBuffer) {
        this(pBuffer.<ResourceLocation, Set<ResourceLocation>>readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation));
    }

    private void m_294838_(FriendlyByteBuf pBuffer) {
        pBuffer.writeCollection(this.features, FriendlyByteBuf::writeResourceLocation);
    }

    @Override
    public PacketType<ClientboundUpdateEnabledFeaturesPacket> write() {
        return ConfigurationPacketTypes.f_316956_;
    }

    public void handle(ClientConfigurationPacketListener pHandler) {
        pHandler.handleEnabledFeatures(this);
    }
}
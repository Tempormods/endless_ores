package net.minecraft.network.protocol.configuration;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public class ConfigurationPacketTypes {
    public static final PacketType<ClientboundFinishConfigurationPacket> f_315770_ = m_324615_("finish_configuration");
    public static final PacketType<ClientboundRegistryDataPacket> f_314997_ = m_324615_("registry_data");
    public static final PacketType<ClientboundUpdateEnabledFeaturesPacket> f_316956_ = m_324615_("update_enabled_features");
    public static final PacketType<ClientboundSelectKnownPacks> f_316719_ = m_324615_("select_known_packs");
    public static final PacketType<ClientboundResetChatPacket> f_315012_ = m_324615_("reset_chat");
    public static final PacketType<ServerboundFinishConfigurationPacket> f_314238_ = m_324489_("finish_configuration");
    public static final PacketType<ServerboundSelectKnownPacks> f_315102_ = m_324489_("select_known_packs");

    private static <T extends Packet<ClientConfigurationPacketListener>> PacketType<T> m_324615_(String p_334889_) {
        return new PacketType<>(PacketFlow.CLIENTBOUND, new ResourceLocation(p_334889_));
    }

    private static <T extends Packet<ServerConfigurationPacketListener>> PacketType<T> m_324489_(String p_334731_) {
        return new PacketType<>(PacketFlow.SERVERBOUND, new ResourceLocation(p_334731_));
    }
}
package net.minecraft.network.protocol.configuration;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.ProtocolInfoBuilder;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ClientboundStoreCookiePacket;
import net.minecraft.network.protocol.common.ClientboundTransferPacket;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.common.CommonPacketTypes;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.cookie.ClientboundCookieRequestPacket;
import net.minecraft.network.protocol.cookie.CookiePacketTypes;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;

public class ConfigurationProtocols {
    public static final ProtocolInfo<ServerConfigurationPacketListener> f_315976_ = ProtocolInfoBuilder.m_323394_(
        ConnectionProtocol.CONFIGURATION,
        p_335753_ -> p_335753_.m_322062_(CommonPacketTypes.f_314731_, ServerboundClientInformationPacket.f_315737_)
                .m_322062_(CookiePacketTypes.f_316493_, ServerboundCookieResponsePacket.f_316817_)
                .m_322062_(CommonPacketTypes.f_314805_, ServerboundCustomPayloadPacket.f_316323_)
                .m_322062_(ConfigurationPacketTypes.f_314238_, ServerboundFinishConfigurationPacket.f_315521_)
                .m_322062_(CommonPacketTypes.f_316196_, ServerboundKeepAlivePacket.f_315984_)
                .m_322062_(CommonPacketTypes.f_316824_, ServerboundPongPacket.f_315035_)
                .m_322062_(CommonPacketTypes.f_316458_, ServerboundResourcePackPacket.f_316982_)
                .m_322062_(ConfigurationPacketTypes.f_315102_, ServerboundSelectKnownPacks.f_314730_)
    );
    public static final ProtocolInfo<ClientConfigurationPacketListener> f_315303_ = ProtocolInfoBuilder.m_322020_(
        ConnectionProtocol.CONFIGURATION,
        p_333514_ -> p_333514_.m_322062_(CookiePacketTypes.f_314706_, ClientboundCookieRequestPacket.f_314850_)
                .m_322062_(CommonPacketTypes.f_314728_, ClientboundCustomPayloadPacket.f_316069_)
                .m_322062_(CommonPacketTypes.f_314152_, ClientboundDisconnectPacket.f_315680_)
                .m_322062_(ConfigurationPacketTypes.f_315770_, ClientboundFinishConfigurationPacket.f_314975_)
                .m_322062_(CommonPacketTypes.f_314082_, ClientboundKeepAlivePacket.f_314816_)
                .m_322062_(CommonPacketTypes.f_316690_, ClientboundPingPacket.f_314346_)
                .m_322062_(ConfigurationPacketTypes.f_315012_, ClientboundResetChatPacket.f_314414_)
                .m_322062_(ConfigurationPacketTypes.f_314997_, ClientboundRegistryDataPacket.f_316830_)
                .m_322062_(CommonPacketTypes.f_315299_, ClientboundResourcePackPopPacket.f_314321_)
                .m_322062_(CommonPacketTypes.f_316687_, ClientboundResourcePackPushPacket.f_314484_)
                .m_322062_(CommonPacketTypes.f_313908_, ClientboundStoreCookiePacket.f_313911_)
                .m_322062_(CommonPacketTypes.f_316077_, ClientboundTransferPacket.f_316509_)
                .m_322062_(ConfigurationPacketTypes.f_316956_, ClientboundUpdateEnabledFeaturesPacket.f_314036_)
                .m_322062_(CommonPacketTypes.f_314377_, ClientboundUpdateTagsPacket.f_313944_)
                .m_322062_(ConfigurationPacketTypes.f_316719_, ClientboundSelectKnownPacks.f_316990_)
    );
}
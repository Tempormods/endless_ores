package net.minecraft.network.protocol.login;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.ProtocolInfoBuilder;
import net.minecraft.network.protocol.cookie.ClientboundCookieRequestPacket;
import net.minecraft.network.protocol.cookie.CookiePacketTypes;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;

public class LoginProtocols {
    public static final ProtocolInfo<ServerLoginPacketListener> f_316141_ = ProtocolInfoBuilder.m_323394_(
        ConnectionProtocol.LOGIN,
        p_331807_ -> p_331807_.m_322062_(LoginPacketTypes.f_315312_, ServerboundHelloPacket.f_316512_)
                .m_322062_(LoginPacketTypes.f_314023_, ServerboundKeyPacket.f_313962_)
                .m_322062_(LoginPacketTypes.f_316432_, ServerboundCustomQueryAnswerPacket.f_315594_)
                .m_322062_(LoginPacketTypes.f_314277_, ServerboundLoginAcknowledgedPacket.f_314115_)
                .m_322062_(CookiePacketTypes.f_316493_, ServerboundCookieResponsePacket.f_316817_)
    );
    public static final ProtocolInfo<ClientLoginPacketListener> f_313900_ = ProtocolInfoBuilder.m_322020_(
        ConnectionProtocol.LOGIN,
        p_329580_ -> p_329580_.m_322062_(LoginPacketTypes.f_314916_, ClientboundLoginDisconnectPacket.f_314580_)
                .m_322062_(LoginPacketTypes.f_314646_, ClientboundHelloPacket.f_317006_)
                .m_322062_(LoginPacketTypes.f_314987_, ClientboundGameProfilePacket.f_315571_)
                .m_322062_(LoginPacketTypes.f_315271_, ClientboundLoginCompressionPacket.f_314441_)
                .m_322062_(LoginPacketTypes.f_315838_, ClientboundCustomQueryPacket.f_317139_)
                .m_322062_(CookiePacketTypes.f_314706_, ClientboundCookieRequestPacket.f_314850_)
    );
}
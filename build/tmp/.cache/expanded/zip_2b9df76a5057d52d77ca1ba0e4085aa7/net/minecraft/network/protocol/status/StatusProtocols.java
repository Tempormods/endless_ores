package net.minecraft.network.protocol.status;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.ProtocolInfoBuilder;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.ping.PingPacketTypes;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;

public class StatusProtocols {
    public static final ProtocolInfo<ServerStatusPacketListener> f_316093_ = ProtocolInfoBuilder.m_323394_(
        ConnectionProtocol.STATUS,
        p_332003_ -> p_332003_.m_322062_(StatusPacketTypes.f_316223_, ServerboundStatusRequestPacket.f_314883_)
                .m_322062_(PingPacketTypes.f_314322_, ServerboundPingRequestPacket.f_314691_)
    );
    public static final ProtocolInfo<ClientStatusPacketListener> f_315277_ = ProtocolInfoBuilder.m_322020_(
        ConnectionProtocol.STATUS,
        p_327697_ -> p_327697_.m_322062_(StatusPacketTypes.f_315020_, ClientboundStatusResponsePacket.f_316394_)
                .m_322062_(PingPacketTypes.f_316097_, ClientboundPongResponsePacket.f_314698_)
    );
}
package net.minecraft.network.protocol.handshake;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.ProtocolInfoBuilder;

public class HandshakeProtocols {
    public static final ProtocolInfo<ServerHandshakePacketListener> f_316563_ = ProtocolInfoBuilder.m_323394_(
        ConnectionProtocol.HANDSHAKING, p_328992_ -> p_328992_.m_322062_(HandshakePacketTypes.f_315563_, ClientIntentionPacket.f_317116_)
    );
}
package net.minecraft.network.protocol.ping;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public class PingPacketTypes {
    public static final PacketType<ClientboundPongResponsePacket> f_316097_ = m_321935_("pong_response");
    public static final PacketType<ServerboundPingRequestPacket> f_314322_ = m_324584_("ping_request");

    private static <T extends Packet<ClientPongPacketListener>> PacketType<T> m_321935_(String p_331071_) {
        return new PacketType<>(PacketFlow.CLIENTBOUND, new ResourceLocation(p_331071_));
    }

    private static <T extends Packet<ServerPingPacketListener>> PacketType<T> m_324584_(String p_330390_) {
        return new PacketType<>(PacketFlow.SERVERBOUND, new ResourceLocation(p_330390_));
    }
}
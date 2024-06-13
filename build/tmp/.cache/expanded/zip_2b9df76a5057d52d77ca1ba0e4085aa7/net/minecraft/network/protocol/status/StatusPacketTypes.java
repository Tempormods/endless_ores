package net.minecraft.network.protocol.status;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public class StatusPacketTypes {
    public static final PacketType<ClientboundStatusResponsePacket> f_315020_ = m_321129_("status_response");
    public static final PacketType<ServerboundStatusRequestPacket> f_316223_ = m_324075_("status_request");

    private static <T extends Packet<ClientStatusPacketListener>> PacketType<T> m_321129_(String p_328780_) {
        return new PacketType<>(PacketFlow.CLIENTBOUND, new ResourceLocation(p_328780_));
    }

    private static <T extends Packet<ServerStatusPacketListener>> PacketType<T> m_324075_(String p_333135_) {
        return new PacketType<>(PacketFlow.SERVERBOUND, new ResourceLocation(p_333135_));
    }
}
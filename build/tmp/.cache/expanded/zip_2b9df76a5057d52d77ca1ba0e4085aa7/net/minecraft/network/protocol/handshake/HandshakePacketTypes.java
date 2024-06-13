package net.minecraft.network.protocol.handshake;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public class HandshakePacketTypes {
    public static final PacketType<ClientIntentionPacket> f_315563_ = m_321735_("intention");

    private static <T extends Packet<ServerHandshakePacketListener>> PacketType<T> m_321735_(String p_329395_) {
        return new PacketType<>(PacketFlow.SERVERBOUND, new ResourceLocation(p_329395_));
    }
}
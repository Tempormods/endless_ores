package net.minecraft.network.protocol.cookie;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public class CookiePacketTypes {
    public static final PacketType<ClientboundCookieRequestPacket> f_314706_ = m_321663_("cookie_request");
    public static final PacketType<ServerboundCookieResponsePacket> f_316493_ = m_322249_("cookie_response");

    private static <T extends Packet<ClientCookiePacketListener>> PacketType<T> m_321663_(String p_334424_) {
        return new PacketType<>(PacketFlow.CLIENTBOUND, new ResourceLocation(p_334424_));
    }

    private static <T extends Packet<ServerCookiePacketListener>> PacketType<T> m_322249_(String p_334543_) {
        return new PacketType<>(PacketFlow.SERVERBOUND, new ResourceLocation(p_334543_));
    }
}
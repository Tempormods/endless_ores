package net.minecraft.network.protocol.login;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public class LoginPacketTypes {
    public static final PacketType<ClientboundCustomQueryPacket> f_315838_ = m_323487_("custom_query");
    public static final PacketType<ClientboundGameProfilePacket> f_314987_ = m_323487_("game_profile");
    public static final PacketType<ClientboundHelloPacket> f_314646_ = m_323487_("hello");
    public static final PacketType<ClientboundLoginCompressionPacket> f_315271_ = m_323487_("login_compression");
    public static final PacketType<ClientboundLoginDisconnectPacket> f_314916_ = m_323487_("login_disconnect");
    public static final PacketType<ServerboundCustomQueryAnswerPacket> f_316432_ = m_319378_("custom_query_answer");
    public static final PacketType<ServerboundHelloPacket> f_315312_ = m_319378_("hello");
    public static final PacketType<ServerboundKeyPacket> f_314023_ = m_319378_("key");
    public static final PacketType<ServerboundLoginAcknowledgedPacket> f_314277_ = m_319378_("login_acknowledged");

    private static <T extends Packet<ClientLoginPacketListener>> PacketType<T> m_323487_(String p_333392_) {
        return new PacketType<>(PacketFlow.CLIENTBOUND, new ResourceLocation(p_333392_));
    }

    private static <T extends Packet<ServerLoginPacketListener>> PacketType<T> m_319378_(String p_335408_) {
        return new PacketType<>(PacketFlow.SERVERBOUND, new ResourceLocation(p_335408_));
    }
}
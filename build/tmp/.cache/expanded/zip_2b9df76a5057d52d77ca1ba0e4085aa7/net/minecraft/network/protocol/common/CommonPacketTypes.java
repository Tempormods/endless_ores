package net.minecraft.network.protocol.common;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public class CommonPacketTypes {
    public static final PacketType<ClientboundCustomPayloadPacket> f_314728_ = m_323017_("custom_payload");
    public static final PacketType<ClientboundDisconnectPacket> f_314152_ = m_323017_("disconnect");
    public static final PacketType<ClientboundKeepAlivePacket> f_314082_ = m_323017_("keep_alive");
    public static final PacketType<ClientboundPingPacket> f_316690_ = m_323017_("ping");
    public static final PacketType<ClientboundResourcePackPopPacket> f_315299_ = m_323017_("resource_pack_pop");
    public static final PacketType<ClientboundResourcePackPushPacket> f_316687_ = m_323017_("resource_pack_push");
    public static final PacketType<ClientboundStoreCookiePacket> f_313908_ = m_323017_("store_cookie");
    public static final PacketType<ClientboundTransferPacket> f_316077_ = m_323017_("transfer");
    public static final PacketType<ClientboundUpdateTagsPacket> f_314377_ = m_323017_("update_tags");
    public static final PacketType<ServerboundClientInformationPacket> f_314731_ = m_322558_("client_information");
    public static final PacketType<ServerboundCustomPayloadPacket> f_314805_ = m_322558_("custom_payload");
    public static final PacketType<ServerboundKeepAlivePacket> f_316196_ = m_322558_("keep_alive");
    public static final PacketType<ServerboundPongPacket> f_316824_ = m_322558_("pong");
    public static final PacketType<ServerboundResourcePackPacket> f_316458_ = m_322558_("resource_pack");

    private static <T extends Packet<ClientCommonPacketListener>> PacketType<T> m_323017_(String p_336356_) {
        return new PacketType<>(PacketFlow.CLIENTBOUND, new ResourceLocation(p_336356_));
    }

    private static <T extends Packet<ServerCommonPacketListener>> PacketType<T> m_322558_(String p_335834_) {
        return new PacketType<>(PacketFlow.SERVERBOUND, new ResourceLocation(p_335834_));
    }
}
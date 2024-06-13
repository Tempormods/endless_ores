package net.minecraft.network.protocol.common;

import net.minecraft.network.ClientboundPacketListener;
import net.minecraft.network.protocol.cookie.ClientCookiePacketListener;

public interface ClientCommonPacketListener extends ClientCookiePacketListener, ClientboundPacketListener {
    void handleKeepAlive(ClientboundKeepAlivePacket pPacket);

    void handlePing(ClientboundPingPacket pPacket);

    void handleCustomPayload(ClientboundCustomPayloadPacket pPacket);

    void handleDisconnect(ClientboundDisconnectPacket pPacket);

    void m_305543_(ClientboundResourcePackPushPacket p_312935_);

    void handleResourcePack(ClientboundResourcePackPopPacket p_311379_);

    void handleUpdateTags(ClientboundUpdateTagsPacket pPacket);

    void m_320373_(ClientboundStoreCookiePacket p_331954_);

    void m_319408_(ClientboundTransferPacket p_329215_);
}
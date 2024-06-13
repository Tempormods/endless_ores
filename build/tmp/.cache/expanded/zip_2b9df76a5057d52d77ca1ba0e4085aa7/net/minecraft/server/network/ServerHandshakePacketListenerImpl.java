package net.minecraft.server.network;

import net.minecraft.SharedConstants;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.LoginProtocols;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.StatusProtocols;
import net.minecraft.server.MinecraftServer;

public class ServerHandshakePacketListenerImpl implements ServerHandshakePacketListener {
    private static final Component IGNORE_STATUS_REASON = Component.translatable("disconnect.ignoring_status_request");
    private final MinecraftServer server;
    private final Connection connection;

    public ServerHandshakePacketListenerImpl(MinecraftServer pServer, Connection pConnection) {
        this.server = pServer;
        this.connection = pConnection;
    }

    @Override
    public void handleIntention(ClientIntentionPacket pPacket) {
        if (!net.minecraftforge.server.ServerLifecycleHooks.handleServerLogin(pPacket, this.connection)) return;
        switch (pPacket.intention()) {
            case LOGIN:
                this.m_323404_(pPacket, false);
                break;
            case STATUS:
                ServerStatus serverstatus = this.server.getStatus();
                this.connection.m_319763_(StatusProtocols.f_315277_);
                if (this.server.repliesToStatus() && serverstatus != null) {
                    this.connection.m_324855_(StatusProtocols.f_316093_, new ServerStatusPacketListenerImpl(serverstatus, this.connection, this.server.getStatusJson()));
                } else {
                    this.connection.disconnect(IGNORE_STATUS_REASON);
                }
                break;
            case TRANSFER:
                if (!this.server.m_320782_()) {
                    this.connection.m_319763_(LoginProtocols.f_313900_);
                    Component component = Component.translatable("multiplayer.disconnect.transfers_disabled");
                    this.connection.send(new ClientboundLoginDisconnectPacket(component));
                    this.connection.disconnect(component);
                } else {
                    this.m_323404_(pPacket, true);
                }
                break;
            default:
                throw new UnsupportedOperationException("Invalid intention " + pPacket.intention());
        }
    }

    private void m_323404_(ClientIntentionPacket p_330592_, boolean p_332714_) {
        this.connection.m_319763_(LoginProtocols.f_313900_);
        if (p_330592_.protocolVersion() != SharedConstants.getCurrentVersion().getProtocolVersion()) {
            Component component;
            if (p_330592_.protocolVersion() < 754) {
                component = Component.translatable("multiplayer.disconnect.outdated_client", SharedConstants.getCurrentVersion().getName());
            } else {
                component = Component.translatable("multiplayer.disconnect.incompatible", SharedConstants.getCurrentVersion().getName());
            }

            this.connection.send(new ClientboundLoginDisconnectPacket(component));
            this.connection.disconnect(component);
        } else {
            this.connection.m_324855_(LoginProtocols.f_316141_, new ServerLoginPacketListenerImpl(this.server, this.connection, p_332714_));
        }
    }

    @Override
    public void onDisconnect(Component pReason) {
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }
}

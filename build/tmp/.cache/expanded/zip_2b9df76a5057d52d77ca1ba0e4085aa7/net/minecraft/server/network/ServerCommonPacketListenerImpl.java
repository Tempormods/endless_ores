package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.util.VisibleForDebug;
import org.slf4j.Logger;

public abstract class ServerCommonPacketListenerImpl implements ServerCommonPacketListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int LATENCY_CHECK_INTERVAL = 15000;
    private static final int f_316947_ = 15000;
    private static final Component TIMEOUT_DISCONNECTION_MESSAGE = Component.translatable("disconnect.timeout");
    static final Component f_315158_ = Component.translatable("multiplayer.disconnect.unexpected_query_response");
    protected final MinecraftServer server;
    protected final Connection connection;
    private final boolean f_314604_;
    private long keepAliveTime;
    private boolean keepAlivePending;
    private long keepAliveChallenge;
    private long f_313967_;
    private boolean f_315218_ = false;
    private int latency;
    private volatile boolean suspendFlushingOnServerThread = false;

    public ServerCommonPacketListenerImpl(MinecraftServer pServer, Connection pConnection, CommonListenerCookie pCookie) {
        this.server = pServer;
        this.connection = pConnection;
        this.keepAliveTime = Util.getMillis();
        this.latency = pCookie.latency();
        this.f_314604_ = pCookie.f_315441_();
    }

    private void m_318670_() {
        if (!this.f_315218_) {
            this.f_313967_ = Util.getMillis();
            this.f_315218_ = true;
        }
    }

    @Override
    public void onDisconnect(Component pReason) {
        if (this.isSingleplayerOwner()) {
            LOGGER.info("Stopping singleplayer server as player logged out");
            this.server.halt(false);
        }
    }

    @Override
    public void handleKeepAlive(ServerboundKeepAlivePacket pPacket) {
        if (this.keepAlivePending && pPacket.getId() == this.keepAliveChallenge) {
            int i = (int)(Util.getMillis() - this.keepAliveTime);
            this.latency = (this.latency * 3 + i) / 4;
            this.keepAlivePending = false;
        } else if (!this.isSingleplayerOwner()) {
            this.disconnect(TIMEOUT_DISCONNECTION_MESSAGE);
        }
    }

    @Override
    public void handlePong(ServerboundPongPacket pPacket) {
    }

    @Override
    public void handleCustomPayload(ServerboundCustomPayloadPacket pPacket) {
        net.minecraftforge.common.ForgeHooks.onCustomPayload(pPacket.payload(), this.connection);
    }

    @Override
    public void handleResourcePackResponse(ServerboundResourcePackPacket pPacket) {
        PacketUtils.ensureRunningOnSameThread(pPacket, this, this.server);
        if (pPacket.action() == ServerboundResourcePackPacket.Action.DECLINED && this.server.isResourcePackRequired()) {
            LOGGER.info("Disconnecting {} due to resource pack {} rejection", this.playerProfile().getName(), pPacket.f_302393_());
            this.disconnect(Component.translatable("multiplayer.requiredTexturePrompt.disconnect"));
        }
    }

    @Override
    public void m_320234_(ServerboundCookieResponsePacket p_335443_) {
        this.disconnect(f_315158_);
    }

    protected void keepConnectionAlive() {
        this.server.getProfiler().push("keepAlive");
        long i = Util.getMillis();
        if (!this.isSingleplayerOwner() && i - this.keepAliveTime >= 15000L) {
            if (this.keepAlivePending) {
                this.disconnect(TIMEOUT_DISCONNECTION_MESSAGE);
            } else if (this.m_319110_(i)) {
                this.keepAlivePending = true;
                this.keepAliveTime = i;
                this.keepAliveChallenge = i;
                this.send(new ClientboundKeepAlivePacket(this.keepAliveChallenge));
            }
        }

        this.server.getProfiler().pop();
    }

    private boolean m_319110_(long p_331601_) {
        if (this.f_315218_) {
            if (p_331601_ - this.f_313967_ >= 15000L) {
                this.disconnect(TIMEOUT_DISCONNECTION_MESSAGE);
            }

            return false;
        } else {
            return true;
        }
    }

    public void suspendFlushing() {
        this.suspendFlushingOnServerThread = true;
    }

    public void resumeFlushing() {
        this.suspendFlushingOnServerThread = false;
        this.connection.flushChannel();
    }

    public void send(Packet<?> pPacket) {
        this.send(pPacket, null);
    }

    public void send(Packet<?> pPacket, @Nullable PacketSendListener pListener) {
        if (pPacket.m_319635_()) {
            this.m_318670_();
        }

        boolean flag = !this.suspendFlushingOnServerThread || !this.server.isSameThread();

        try {
            this.connection.send(pPacket, pListener, flag);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Sending packet");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Packet being sent");
            crashreportcategory.setDetail("Packet class", () -> pPacket.getClass().getCanonicalName());
            throw new ReportedException(crashreport);
        }
    }

    public void disconnect(Component pReason) {
        this.connection.send(new ClientboundDisconnectPacket(pReason), PacketSendListener.thenRun(() -> this.connection.disconnect(pReason)));
        this.connection.setReadOnly();
        this.server.executeBlocking(this.connection::handleDisconnection);
    }

    protected boolean isSingleplayerOwner() {
        return this.server.isSingleplayerOwner(this.playerProfile());
    }

    protected abstract GameProfile playerProfile();

    @VisibleForDebug
    public GameProfile getOwner() {
        return this.playerProfile();
    }

    public int latency() {
        return this.latency;
    }

    protected CommonListenerCookie createCookie(ClientInformation pClientInformation) {
        return new CommonListenerCookie(this.playerProfile(), this.latency, pClientInformation, this.f_314604_);
    }

    public Connection getConnection() {
        return this.connection;
    }
}

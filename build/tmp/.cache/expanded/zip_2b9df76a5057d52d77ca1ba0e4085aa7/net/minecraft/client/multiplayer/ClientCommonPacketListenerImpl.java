package net.minecraft.client.multiplayer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReportCategory;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.resources.server.DownloadedPackSource;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;
import net.minecraft.network.Connection;
import net.minecraft.network.ServerboundPacketListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ClientboundStoreCookiePacket;
import net.minecraft.network.protocol.common.ClientboundTransferPacket;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.network.protocol.cookie.ClientboundCookieRequestPacket;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public abstract class ClientCommonPacketListenerImpl implements ClientCommonPacketListener {
    private static final Component GENERIC_DISCONNECT_MESSAGE = Component.translatable("disconnect.lost");
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final Minecraft minecraft;
    protected final Connection connection;
    @Nullable
    protected final ServerData serverData;
    @Nullable
    protected String serverBrand;
    protected final WorldSessionTelemetryManager telemetryManager;
    @Nullable
    protected final Screen postDisconnectScreen;
    protected boolean f_313904_;
    @Deprecated(
        forRemoval = true
    )
    protected final boolean f_316112_;
    private final List<ClientCommonPacketListenerImpl.DeferredPacket> deferredPackets = new ArrayList<>();
    protected final Map<ResourceLocation, byte[]> f_316471_;

    protected ClientCommonPacketListenerImpl(Minecraft pMinecraft, Connection pConnection, CommonListenerCookie pCommonListenerCookie) {
        this.minecraft = pMinecraft;
        this.connection = pConnection;
        this.serverData = pCommonListenerCookie.serverData();
        this.serverBrand = pCommonListenerCookie.serverBrand();
        this.telemetryManager = pCommonListenerCookie.telemetryManager();
        this.postDisconnectScreen = pCommonListenerCookie.postDisconnectScreen();
        this.f_316471_ = pCommonListenerCookie.f_314268_();
        this.f_316112_ = pCommonListenerCookie.f_317100_();
    }

    @Override
    public void m_322364_(Packet p_333124_, Exception p_332903_) {
        LOGGER.error("Failed to handle packet {}", p_333124_, p_332903_);
        if (this.f_316112_) {
            this.connection.disconnect(Component.translatable("disconnect.packetError"));
        }
    }

    @Override
    public boolean shouldHandleMessage(Packet<?> p_332498_) {
        return ClientCommonPacketListener.super.shouldHandleMessage(p_332498_)
            ? true
            : this.f_313904_ && (p_332498_ instanceof ClientboundStoreCookiePacket || p_332498_ instanceof ClientboundTransferPacket);
    }

    @Override
    public void handleKeepAlive(ClientboundKeepAlivePacket pPacket) {
        this.sendWhen(new ServerboundKeepAlivePacket(pPacket.getId()), () -> !RenderSystem.isFrozenAtPollEvents(), Duration.ofMinutes(1L));
    }

    @Override
    public void handlePing(ClientboundPingPacket pPacket) {
        PacketUtils.ensureRunningOnSameThread(pPacket, this, this.minecraft);
        this.send(new ServerboundPongPacket(pPacket.getId()));
    }

    @Override
    public void handleCustomPayload(ClientboundCustomPayloadPacket pPacket) {
        if (net.minecraftforge.common.ForgeHooks.onCustomPayload(pPacket.payload(), this.connection)) return;
        CustomPacketPayload custompacketpayload = pPacket.payload();
        if (!(custompacketpayload instanceof DiscardedPayload)) {
            PacketUtils.ensureRunningOnSameThread(pPacket, this, this.minecraft);
            if (custompacketpayload instanceof BrandPayload brandpayload) {
                this.serverBrand = brandpayload.brand();
                this.telemetryManager.onServerBrandReceived(brandpayload.brand());
            } else {
                this.handleCustomPayload(custompacketpayload);
            }
        }
    }

    protected abstract void handleCustomPayload(CustomPacketPayload pPayload);

    @Override
    public void m_305543_(ClientboundResourcePackPushPacket p_310071_) {
        PacketUtils.ensureRunningOnSameThread(p_310071_, this, this.minecraft);
        UUID uuid = p_310071_.f_303323_();
        URL url = parseResourcePackUrl(p_310071_.f_302762_());
        if (url == null) {
            this.connection.send(new ServerboundResourcePackPacket(uuid, ServerboundResourcePackPacket.Action.INVALID_URL));
        } else {
            String s = p_310071_.f_302355_();
            boolean flag = p_310071_.f_302925_();
            ServerData.ServerPackStatus serverdata$serverpackstatus = this.serverData != null ? this.serverData.getResourcePackStatus() : ServerData.ServerPackStatus.PROMPT;
            if (serverdata$serverpackstatus != ServerData.ServerPackStatus.PROMPT
                && (!flag || serverdata$serverpackstatus != ServerData.ServerPackStatus.DISABLED)) {
                this.minecraft.getDownloadedPackSource().m_304637_(uuid, url, s);
            } else {
                this.minecraft.setScreen(this.m_306760_(uuid, url, s, flag, p_310071_.f_303164_().orElse(null)));
            }
        }
    }

    @Override
    public void handleResourcePack(ClientboundResourcePackPopPacket p_311803_) {
        PacketUtils.ensureRunningOnSameThread(p_311803_, this, this.minecraft);
        p_311803_.f_302279_().ifPresentOrElse(p_308277_ -> this.minecraft.getDownloadedPackSource().m_306043_(p_308277_), () -> this.minecraft.getDownloadedPackSource().m_304654_());
    }

    static Component preparePackPrompt(Component pLine1, @Nullable Component pLine2) {
        return (Component)(pLine2 == null ? pLine1 : Component.translatable("multiplayer.texturePrompt.serverPrompt", pLine1, pLine2));
    }

    @Nullable
    private static URL parseResourcePackUrl(String pUrl) {
        try {
            URL url = new URL(pUrl);
            String s = url.getProtocol();
            return !"http".equals(s) && !"https".equals(s) ? null : url;
        } catch (MalformedURLException malformedurlexception) {
            return null;
        }
    }

    @Override
    public void m_320309_(ClientboundCookieRequestPacket p_328943_) {
        PacketUtils.ensureRunningOnSameThread(p_328943_, this, this.minecraft);
        this.connection.send(new ServerboundCookieResponsePacket(p_328943_.f_315050_(), this.f_316471_.get(p_328943_.f_315050_())));
    }

    @Override
    public void m_320373_(ClientboundStoreCookiePacket p_333290_) {
        PacketUtils.ensureRunningOnSameThread(p_333290_, this, this.minecraft);
        this.f_316471_.put(p_333290_.f_314603_(), p_333290_.f_314170_());
    }

    @Override
    public void m_319408_(ClientboundTransferPacket p_332424_) {
        this.f_313904_ = true;
        PacketUtils.ensureRunningOnSameThread(p_332424_, this, this.minecraft);
        if (this.serverData == null) {
            throw new IllegalStateException("Cannot transfer to server from singleplayer");
        } else {
            this.connection.disconnect(Component.translatable("disconnect.transfer"));
            this.connection.setReadOnly();
            this.connection.handleDisconnection();
            ServerAddress serveraddress = new ServerAddress(p_332424_.f_314661_(), p_332424_.f_316558_());
            ConnectScreen.startConnecting(
                Objects.requireNonNullElseGet(this.postDisconnectScreen, TitleScreen::new),
                this.minecraft,
                serveraddress,
                this.serverData,
                false,
                new TransferState(this.f_316471_)
            );
        }
    }

    @Override
    public void handleDisconnect(ClientboundDisconnectPacket pPacket) {
        this.connection.disconnect(pPacket.reason());
    }

    protected void sendDeferredPackets() {
        Iterator<ClientCommonPacketListenerImpl.DeferredPacket> iterator = this.deferredPackets.iterator();

        while (iterator.hasNext()) {
            ClientCommonPacketListenerImpl.DeferredPacket clientcommonpacketlistenerimpl$deferredpacket = iterator.next();
            if (clientcommonpacketlistenerimpl$deferredpacket.sendCondition().getAsBoolean()) {
                this.send(clientcommonpacketlistenerimpl$deferredpacket.packet);
                iterator.remove();
            } else if (clientcommonpacketlistenerimpl$deferredpacket.expirationTime() <= Util.getMillis()) {
                iterator.remove();
            }
        }
    }

    public void send(Packet<?> pPacket) {
        this.connection.send(pPacket);
    }

    @Override
    public void onDisconnect(Component pReason) {
        this.telemetryManager.onDisconnect();
        this.minecraft.m_322774_(this.createDisconnectScreen(pReason), this.f_313904_);
        LOGGER.warn("Client disconnected with reason: {}", pReason.getString());
    }

    @Override
    public void m_306579_(CrashReportCategory p_309761_) {
        p_309761_.setDetail("Server type", () -> this.serverData != null ? this.serverData.m_306276_().toString() : "<none>");
        p_309761_.setDetail("Server brand", () -> this.serverBrand);
    }

    protected Screen createDisconnectScreen(Component pReason) {
        Screen screen = Objects.requireNonNullElseGet(this.postDisconnectScreen, () -> new JoinMultiplayerScreen(new TitleScreen()));
        return (Screen)(this.serverData != null && this.serverData.isRealm()
            ? new DisconnectedRealmsScreen(screen, GENERIC_DISCONNECT_MESSAGE, pReason)
            : new DisconnectedScreen(screen, GENERIC_DISCONNECT_MESSAGE, pReason));
    }

    @Nullable
    public String serverBrand() {
        return this.serverBrand;
    }

    private void sendWhen(Packet<? extends ServerboundPacketListener> pPacket, BooleanSupplier pSendCondition, Duration pExpirationTime) {
        if (pSendCondition.getAsBoolean()) {
            this.send(pPacket);
        } else {
            this.deferredPackets.add(new ClientCommonPacketListenerImpl.DeferredPacket(pPacket, pSendCondition, Util.getMillis() + pExpirationTime.toMillis()));
        }
    }

    private Screen m_306760_(UUID p_313077_, URL p_312880_, String p_309420_, boolean p_312218_, @Nullable Component p_309535_) {
        Screen screen = this.minecraft.screen;
        return screen instanceof ClientCommonPacketListenerImpl.PackConfirmScreen clientcommonpacketlistenerimpl$packconfirmscreen
            ? clientcommonpacketlistenerimpl$packconfirmscreen.m_307179_(this.minecraft, p_313077_, p_312880_, p_309420_, p_312218_, p_309535_)
            : new ClientCommonPacketListenerImpl.PackConfirmScreen(
                this.minecraft,
                screen,
                List.of(new ClientCommonPacketListenerImpl.PackConfirmScreen.PendingRequest(p_313077_, p_312880_, p_309420_)),
                p_312218_,
                p_309535_
            );
    }

    @OnlyIn(Dist.CLIENT)
    static record DeferredPacket(Packet<? extends ServerboundPacketListener> packet, BooleanSupplier sendCondition, long expirationTime) {
    }

    @OnlyIn(Dist.CLIENT)
    class PackConfirmScreen extends ConfirmScreen {
        private final List<ClientCommonPacketListenerImpl.PackConfirmScreen.PendingRequest> f_303444_;
        @Nullable
        private final Screen f_302369_;

        PackConfirmScreen(
            final Minecraft p_309743_,
            @Nullable final Screen p_312679_,
            final List<ClientCommonPacketListenerImpl.PackConfirmScreen.PendingRequest> p_312458_,
            final boolean p_313140_,
            @Nullable final Component p_312901_
        ) {
            super(
                p_309396_ -> {
                    p_309743_.setScreen(p_312679_);
                    DownloadedPackSource downloadedpacksource = p_309743_.getDownloadedPackSource();
                    if (p_309396_) {
                        if (ClientCommonPacketListenerImpl.this.serverData != null) {
                            ClientCommonPacketListenerImpl.this.serverData.setResourcePackStatus(ServerData.ServerPackStatus.ENABLED);
                        }

                        downloadedpacksource.m_307345_();
                    } else {
                        downloadedpacksource.m_306322_();
                        if (p_313140_) {
                            ClientCommonPacketListenerImpl.this.connection.disconnect(Component.translatable("multiplayer.requiredTexturePrompt.disconnect"));
                        } else if (ClientCommonPacketListenerImpl.this.serverData != null) {
                            ClientCommonPacketListenerImpl.this.serverData.setResourcePackStatus(ServerData.ServerPackStatus.DISABLED);
                        }
                    }

                    for (ClientCommonPacketListenerImpl.PackConfirmScreen.PendingRequest clientcommonpacketlistenerimpl$packconfirmscreen$pendingrequest : p_312458_) {
                        downloadedpacksource.m_304637_(
                            clientcommonpacketlistenerimpl$packconfirmscreen$pendingrequest.f_303468_,
                            clientcommonpacketlistenerimpl$packconfirmscreen$pendingrequest.f_303036_,
                            clientcommonpacketlistenerimpl$packconfirmscreen$pendingrequest.f_303850_
                        );
                    }

                    if (ClientCommonPacketListenerImpl.this.serverData != null) {
                        ServerList.saveSingleServer(ClientCommonPacketListenerImpl.this.serverData);
                    }
                },
                p_313140_ ? Component.translatable("multiplayer.requiredTexturePrompt.line1") : Component.translatable("multiplayer.texturePrompt.line1"),
                ClientCommonPacketListenerImpl.preparePackPrompt(
                    p_313140_
                        ? Component.translatable("multiplayer.requiredTexturePrompt.line2").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)
                        : Component.translatable("multiplayer.texturePrompt.line2"),
                    p_312901_
                ),
                p_313140_ ? CommonComponents.GUI_PROCEED : CommonComponents.GUI_YES,
                p_313140_ ? CommonComponents.GUI_DISCONNECT : CommonComponents.GUI_NO
            );
            this.f_303444_ = p_312458_;
            this.f_302369_ = p_312679_;
        }

        public ClientCommonPacketListenerImpl.PackConfirmScreen m_307179_(
            Minecraft p_312486_, UUID p_311436_, URL p_309404_, String p_312909_, boolean p_312985_, @Nullable Component p_309496_
        ) {
            List<ClientCommonPacketListenerImpl.PackConfirmScreen.PendingRequest> list = ImmutableList.<ClientCommonPacketListenerImpl.PackConfirmScreen.PendingRequest>builderWithExpectedSize(
                    this.f_303444_.size() + 1
                )
                .addAll(this.f_303444_)
                .add(new ClientCommonPacketListenerImpl.PackConfirmScreen.PendingRequest(p_311436_, p_309404_, p_312909_))
                .build();
            return ClientCommonPacketListenerImpl.this.new PackConfirmScreen(p_312486_, this.f_302369_, list, p_312985_, p_309496_);
        }

        @OnlyIn(Dist.CLIENT)
        static record PendingRequest(UUID f_303468_, URL f_303036_, String f_303850_) {
        }
    }
}

package net.minecraft.client.multiplayer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.ForcedUsernameChangeException;
import com.mojang.authlib.exceptions.InsufficientPrivilegesException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.exceptions.UserBannedException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.security.PublicKey;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.CrashReportCategory;
import net.minecraft.Util;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.configuration.ConfigurationProtocols;
import net.minecraft.network.protocol.cookie.ClientboundCookieRequestPacket;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import net.minecraft.network.protocol.login.ClientboundLoginCompressionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import net.minecraft.network.protocol.login.ServerboundLoginAcknowledgedPacket;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Crypt;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientHandshakePacketListenerImpl implements ClientLoginPacketListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Minecraft minecraft;
    @Nullable
    private final ServerData serverData;
    @Nullable
    private final Screen parent;
    private final Consumer<Component> updateStatus;
    private final Connection connection;
    private final boolean newWorld;
    @Nullable
    private final Duration worldLoadDuration;
    @Nullable
    private String minigameName;
    private final Map<ResourceLocation, byte[]> f_314651_;
    private final boolean f_315773_;
    private final AtomicReference<ClientHandshakePacketListenerImpl.State> state = new AtomicReference<>(ClientHandshakePacketListenerImpl.State.CONNECTING);

    public ClientHandshakePacketListenerImpl(
        Connection pConnection,
        Minecraft pMinecraft,
        @Nullable ServerData pServerData,
        @Nullable Screen pParent,
        boolean pNewWorld,
        @Nullable Duration pWorldLoadDuration,
        Consumer<Component> pUpdateStatus,
        @Nullable TransferState p_332707_
    ) {
        this.connection = pConnection;
        this.minecraft = pMinecraft;
        this.serverData = pServerData;
        this.parent = pParent;
        this.updateStatus = pUpdateStatus;
        this.newWorld = pNewWorld;
        this.worldLoadDuration = pWorldLoadDuration;
        this.f_314651_ = p_332707_ != null ? new HashMap<>(p_332707_.f_315177_()) : new HashMap<>();
        this.f_315773_ = p_332707_ != null;
    }

    private void switchState(ClientHandshakePacketListenerImpl.State pState) {
        ClientHandshakePacketListenerImpl.State clienthandshakepacketlistenerimpl$state = this.state.updateAndGet(p_325472_ -> {
            if (!pState.fromStates.contains(p_325472_)) {
                throw new IllegalStateException("Tried to switch to " + pState + " from " + p_325472_ + ", but expected one of " + pState.fromStates);
            } else {
                return pState;
            }
        });
        this.updateStatus.accept(clienthandshakepacketlistenerimpl$state.message);
    }

    @Override
    public void handleHello(ClientboundHelloPacket pPacket) {
        this.switchState(ClientHandshakePacketListenerImpl.State.AUTHORIZING);

        Cipher cipher;
        Cipher cipher1;
        String s;
        ServerboundKeyPacket serverboundkeypacket;
        try {
            SecretKey secretkey = Crypt.generateSecretKey();
            PublicKey publickey = pPacket.getPublicKey();
            s = new BigInteger(Crypt.digestData(pPacket.getServerId(), publickey, secretkey)).toString(16);
            cipher = Crypt.getCipher(2, secretkey);
            cipher1 = Crypt.getCipher(1, secretkey);
            byte[] abyte = pPacket.getChallenge();
            serverboundkeypacket = new ServerboundKeyPacket(secretkey, publickey, abyte);
        } catch (Exception exception) {
            throw new IllegalStateException("Protocol error", exception);
        }

        if (pPacket.m_319283_()) {
            Util.ioPool().submit(() -> {
                Component component = this.authenticateServer(s);
                if (component != null) {
                    if (this.serverData == null || !this.serverData.isLan()) {
                        this.connection.disconnect(component);
                        return;
                    }

                    LOGGER.warn(component.getString());
                }

                this.m_324301_(serverboundkeypacket, cipher, cipher1);
            });
        } else {
            this.m_324301_(serverboundkeypacket, cipher, cipher1);
        }
    }

    private void m_324301_(ServerboundKeyPacket p_333847_, Cipher p_327699_, Cipher p_330168_) {
        this.switchState(ClientHandshakePacketListenerImpl.State.ENCRYPTING);
        this.connection.send(p_333847_, PacketSendListener.thenRun(() -> this.connection.setEncryptionKey(p_327699_, p_330168_)));
    }

    @Nullable
    private Component authenticateServer(String pServerHash) {
        try {
            this.getMinecraftSessionService().joinServer(this.minecraft.getUser().getProfileId(), this.minecraft.getUser().getAccessToken(), pServerHash);
            return null;
        } catch (AuthenticationUnavailableException authenticationunavailableexception) {
            return Component.translatable("disconnect.loginFailedInfo", Component.translatable("disconnect.loginFailedInfo.serversUnavailable"));
        } catch (InvalidCredentialsException invalidcredentialsexception) {
            return Component.translatable("disconnect.loginFailedInfo", Component.translatable("disconnect.loginFailedInfo.invalidSession"));
        } catch (InsufficientPrivilegesException insufficientprivilegesexception) {
            return Component.translatable("disconnect.loginFailedInfo", Component.translatable("disconnect.loginFailedInfo.insufficientPrivileges"));
        } catch (ForcedUsernameChangeException | UserBannedException userbannedexception) {
            return Component.translatable("disconnect.loginFailedInfo", Component.translatable("disconnect.loginFailedInfo.userBanned"));
        } catch (AuthenticationException authenticationexception) {
            return Component.translatable("disconnect.loginFailedInfo", authenticationexception.getMessage());
        }
    }

    private MinecraftSessionService getMinecraftSessionService() {
        return this.minecraft.getMinecraftSessionService();
    }

    @Override
    public void handleGameProfile(ClientboundGameProfilePacket pPacket) {
        this.switchState(ClientHandshakePacketListenerImpl.State.JOINING);
        GameProfile gameprofile = pPacket.gameProfile();
        this.connection
            .m_324855_(
                ConfigurationProtocols.f_315303_,
                new ClientConfigurationPacketListenerImpl(
                    this.minecraft,
                    this.connection,
                    new CommonListenerCookie(
                        gameprofile,
                        this.minecraft.getTelemetryManager().createWorldSessionManager(this.newWorld, this.worldLoadDuration, this.minigameName),
                        ClientRegistryLayer.createRegistryAccess().compositeAccess(),
                        FeatureFlags.DEFAULT_FLAGS,
                        null,
                        this.serverData,
                        this.parent,
                        this.f_314651_,
                        null,
                        pPacket.f_315909_()
                    )
                )
            );
        this.connection.send(ServerboundLoginAcknowledgedPacket.f_314965_);
        this.connection.m_319763_(ConfigurationProtocols.f_315976_);
        this.connection.send(new ServerboundCustomPayloadPacket(new BrandPayload(ClientBrandRetriever.getClientModName())));
        this.connection.send(new ServerboundClientInformationPacket(this.minecraft.options.buildPlayerInformation()));
    }

    @Override
    public void onDisconnect(Component pReason) {
        Component component = this.f_315773_ ? CommonComponents.f_317038_ : CommonComponents.CONNECT_FAILED;
        if (this.serverData != null && this.serverData.isRealm()) {
            this.minecraft.setScreen(new DisconnectedRealmsScreen(this.parent, component, pReason));
        } else if (net.minecraftforge.client.ForgeHooksClient.onClientDisconnect(this.connection, this.minecraft, this.parent, pReason)) {
        } else {
            this.minecraft.setScreen(new DisconnectedScreen(this.parent, component, pReason));
        }
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }

    @Override
    public void handleDisconnect(ClientboundLoginDisconnectPacket pPacket) {
        this.connection.disconnect(pPacket.getReason());
    }

    @Override
    public void handleCompression(ClientboundLoginCompressionPacket pPacket) {
        if (!this.connection.isMemoryConnection()) {
            this.connection.setupCompression(pPacket.getCompressionThreshold(), false);
        }
    }

    @Override
    public void handleCustomQuery(ClientboundCustomQueryPacket pPacket) {
        if (net.minecraftforge.common.ForgeHooks.onCustomPayload(pPacket, this.connection)) return;
        this.updateStatus.accept(Component.translatable("connect.negotiating"));
        this.connection.send(new ServerboundCustomQueryAnswerPacket(pPacket.transactionId(), null));
    }

    public void setMinigameName(@Nullable String pMinigameName) {
        this.minigameName = pMinigameName;
    }

    @Override
    public void m_320309_(ClientboundCookieRequestPacket p_328065_) {
        this.connection.send(new ServerboundCookieResponsePacket(p_328065_.f_315050_(), this.f_314651_.get(p_328065_.f_315050_())));
    }

    @Override
    public void m_306579_(CrashReportCategory p_311844_) {
        p_311844_.setDetail("Server type", () -> this.serverData != null ? this.serverData.m_306276_().toString() : "<unknown>");
        p_311844_.setDetail("Login phase", () -> this.state.get().toString());
    }

    @OnlyIn(Dist.CLIENT)
    static enum State {
        CONNECTING(Component.translatable("connect.connecting"), Set.of()),
        AUTHORIZING(Component.translatable("connect.authorizing"), Set.of(CONNECTING)),
        ENCRYPTING(Component.translatable("connect.encrypting"), Set.of(AUTHORIZING)),
        JOINING(Component.translatable("connect.joining"), Set.of(ENCRYPTING, CONNECTING));

        final Component message;
        final Set<ClientHandshakePacketListenerImpl.State> fromStates;

        private State(final Component pMessage, final Set<ClientHandshakePacketListenerImpl.State> pFromStates) {
            this.message = pMessage;
            this.fromStates = pFromStates;
        }
    }
}

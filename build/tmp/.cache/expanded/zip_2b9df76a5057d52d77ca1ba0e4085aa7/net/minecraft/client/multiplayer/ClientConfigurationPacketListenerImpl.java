package net.minecraft.client.multiplayer;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ClientConfigurationPacketListener;
import net.minecraft.network.protocol.configuration.ClientboundFinishConfigurationPacket;
import net.minecraft.network.protocol.configuration.ClientboundRegistryDataPacket;
import net.minecraft.network.protocol.configuration.ClientboundResetChatPacket;
import net.minecraft.network.protocol.configuration.ClientboundSelectKnownPacks;
import net.minecraft.network.protocol.configuration.ClientboundUpdateEnabledFeaturesPacket;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;
import net.minecraft.network.protocol.configuration.ServerboundSelectKnownPacks;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientConfigurationPacketListenerImpl extends ClientCommonPacketListenerImpl implements ClientConfigurationPacketListener, TickablePacketListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final GameProfile localGameProfile;
    private FeatureFlagSet enabledFeatures;
    private final RegistryAccess.Frozen receivedRegistries;
    private final RegistryDataCollector f_314792_ = new RegistryDataCollector();
    @Nullable
    private KnownPacksManager f_315686_;
    @Nullable
    protected ChatComponent.State f_316283_;

    public ClientConfigurationPacketListenerImpl(Minecraft pMinecraft, Connection pConnection, CommonListenerCookie pCommonListenerCookie) {
        super(pMinecraft, pConnection, pCommonListenerCookie);
        this.localGameProfile = pCommonListenerCookie.localGameProfile();
        this.receivedRegistries = pCommonListenerCookie.receivedRegistries();
        this.enabledFeatures = pCommonListenerCookie.enabledFeatures();
        this.f_316283_ = pCommonListenerCookie.f_315631_();
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }

    @Override
    protected void handleCustomPayload(CustomPacketPayload pPayload) {
        this.handleUnknownCustomPayload(pPayload);
    }

    private void handleUnknownCustomPayload(CustomPacketPayload pPayload) {
        LOGGER.warn("Unknown custom packet payload: {}", pPayload.m_293297_().f_314054_());
    }

    @Override
    public void handleRegistryData(ClientboundRegistryDataPacket pPacket) {
        PacketUtils.ensureRunningOnSameThread(pPacket, this, this.minecraft);
        this.f_314792_.m_320164_(pPacket.f_316367_(), pPacket.f_315072_());
    }

    @Override
    public void handleUpdateTags(ClientboundUpdateTagsPacket p_335168_) {
        PacketUtils.ensureRunningOnSameThread(p_335168_, this, this.minecraft);
        this.f_314792_.m_319875_(p_335168_.getTags());
    }

    @Override
    public void handleEnabledFeatures(ClientboundUpdateEnabledFeaturesPacket pPacket) {
        this.enabledFeatures = FeatureFlags.REGISTRY.fromNames(pPacket.features());
    }

    @Override
    public void m_319752_(ClientboundSelectKnownPacks p_333075_) {
        PacketUtils.ensureRunningOnSameThread(p_333075_, this, this.minecraft);
        if (this.f_315686_ == null) {
            this.f_315686_ = new KnownPacksManager();
        }

        List<KnownPack> list = this.f_315686_.m_323907_(p_333075_.f_315150_());
        this.send(new ServerboundSelectKnownPacks(list));
    }

    @Override
    public void m_318905_(ClientboundResetChatPacket p_328730_) {
        this.f_316283_ = null;
    }

    private <T> T m_324928_(Function<ResourceProvider, T> p_330303_) {
        if (this.f_315686_ == null) {
            return p_330303_.apply(ResourceProvider.f_315846_);
        } else {
            Object object;
            try (CloseableResourceManager closeableresourcemanager = this.f_315686_.m_323502_()) {
                object = p_330303_.apply(closeableresourcemanager);
            }

            return (T)object;
        }
    }

    @Override
    public void handleConfigurationFinished(ClientboundFinishConfigurationPacket pPacket) {
        PacketUtils.ensureRunningOnSameThread(pPacket, this, this.minecraft);
        RegistryAccess.Frozen registryaccess$frozen = this.m_324928_(
            p_325470_ -> this.f_314792_.m_323733_(p_325470_, this.receivedRegistries, this.connection.isMemoryConnection())
        );
        this.connection
            .m_324855_(
                GameProtocols.f_315024_.m_324476_(RegistryFriendlyByteBuf.m_324635_(registryaccess$frozen)),
                new ClientPacketListener(
                    this.minecraft,
                    this.connection,
                    new CommonListenerCookie(
                        this.localGameProfile,
                        this.telemetryManager,
                        registryaccess$frozen,
                        this.enabledFeatures,
                        this.serverBrand,
                        this.serverData,
                        this.postDisconnectScreen,
                        this.f_316471_,
                        this.f_316283_,
                        this.f_316112_
                    )
                )
            );
        this.connection.send(ServerboundFinishConfigurationPacket.f_315825_);
        this.connection.m_319763_(GameProtocols.f_315992_.m_324476_(RegistryFriendlyByteBuf.m_324635_(registryaccess$frozen)));
        net.minecraftforge.common.ForgeHooks.handleClientConfigurationComplete(this.connection);
    }

    @Override
    public void tick() {
        this.sendDeferredPackets();
    }

    @Override
    public void onDisconnect(Component p_311878_) {
        super.onDisconnect(p_311878_);
        this.minecraft.m_305121_();
    }
}

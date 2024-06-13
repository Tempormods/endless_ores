package com.mojang.realmsclient;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsNotification;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RegionPingResult;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.RealmsServerList;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsCreateRealmScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
import com.mojang.realmsclient.gui.screens.RealmsPopupScreen;
import com.mojang.realmsclient.gui.task.DataFetcher;
import com.mojang.realmsclient.util.RealmsPersistence;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.PopupScreen;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.CommonLinks;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsMainScreen extends RealmsScreen {
    static final ResourceLocation INFO_SPRITE = new ResourceLocation("icon/info");
    static final ResourceLocation NEW_REALM_SPRITE = new ResourceLocation("icon/new_realm");
    static final ResourceLocation EXPIRED_SPRITE = new ResourceLocation("realm_status/expired");
    static final ResourceLocation EXPIRES_SOON_SPRITE = new ResourceLocation("realm_status/expires_soon");
    static final ResourceLocation OPEN_SPRITE = new ResourceLocation("realm_status/open");
    static final ResourceLocation CLOSED_SPRITE = new ResourceLocation("realm_status/closed");
    private static final ResourceLocation INVITE_SPRITE = new ResourceLocation("icon/invite");
    private static final ResourceLocation NEWS_SPRITE = new ResourceLocation("icon/news");
    static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation LOGO_LOCATION = new ResourceLocation("textures/gui/title/realms.png");
    private static final ResourceLocation NO_REALMS_LOCATION = new ResourceLocation("textures/gui/realms/no_realms.png");
    private static final Component TITLE = Component.translatable("menu.online");
    private static final Component LOADING_TEXT = Component.translatable("mco.selectServer.loading");
    static final Component SERVER_UNITIALIZED_TEXT = Component.translatable("mco.selectServer.uninitialized");
    static final Component SUBSCRIPTION_EXPIRED_TEXT = Component.translatable("mco.selectServer.expiredList");
    private static final Component SUBSCRIPTION_RENEW_TEXT = Component.translatable("mco.selectServer.expiredRenew");
    static final Component TRIAL_EXPIRED_TEXT = Component.translatable("mco.selectServer.expiredTrial");
    private static final Component PLAY_TEXT = Component.translatable("mco.selectServer.play");
    private static final Component LEAVE_SERVER_TEXT = Component.translatable("mco.selectServer.leave");
    private static final Component CONFIGURE_SERVER_TEXT = Component.translatable("mco.selectServer.configure");
    static final Component SERVER_EXPIRED_TOOLTIP = Component.translatable("mco.selectServer.expired");
    static final Component SERVER_EXPIRES_SOON_TOOLTIP = Component.translatable("mco.selectServer.expires.soon");
    static final Component SERVER_EXPIRES_IN_DAY_TOOLTIP = Component.translatable("mco.selectServer.expires.day");
    static final Component SERVER_OPEN_TOOLTIP = Component.translatable("mco.selectServer.open");
    static final Component SERVER_CLOSED_TOOLTIP = Component.translatable("mco.selectServer.closed");
    static final Component UNITIALIZED_WORLD_NARRATION = Component.translatable("gui.narrate.button", SERVER_UNITIALIZED_TEXT);
    private static final Component NO_REALMS_TEXT = Component.translatable("mco.selectServer.noRealms");
    private static final Component NO_PENDING_INVITES = Component.translatable("mco.invites.nopending");
    private static final Component PENDING_INVITES = Component.translatable("mco.invites.pending");
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_COLUMNS = 3;
    private static final int BUTTON_SPACING = 4;
    private static final int CONTENT_WIDTH = 308;
    private static final int LOGO_WIDTH = 128;
    private static final int LOGO_HEIGHT = 34;
    private static final int LOGO_TEXTURE_WIDTH = 128;
    private static final int LOGO_TEXTURE_HEIGHT = 64;
    private static final int LOGO_PADDING = 5;
    private static final int HEADER_HEIGHT = 44;
    private static final int FOOTER_PADDING = 11;
    private static final int f_303294_ = 40;
    private static final int f_303305_ = 20;
    private static final int ENTRY_WIDTH = 216;
    private static final int ITEM_HEIGHT = 36;
    private static final boolean f_302464_ = !SharedConstants.getCurrentVersion().isStable();
    private static boolean f_303830_ = f_302464_;
    private final CompletableFuture<RealmsAvailability.Result> availability = RealmsAvailability.get();
    @Nullable
    private DataFetcher.Subscription dataSubscription;
    private final Set<UUID> handledSeenNotifications = new HashSet<>();
    private static boolean regionsPinged;
    private final RateLimiter inviteNarrationLimiter;
    private final Screen lastScreen;
    private Button playButton;
    private Button backButton;
    private Button renewButton;
    private Button configureButton;
    private Button leaveButton;
    RealmsMainScreen.RealmSelectionList realmSelectionList;
    private RealmsServerList serverList;
    private List<RealmsServer> f_302288_ = List.of();
    private volatile boolean trialsAvailable;
    @Nullable
    private volatile String newsLink;
    long lastClickTime;
    private final List<RealmsNotification> notifications = new ArrayList<>();
    private Button addRealmButton;
    private RealmsMainScreen.NotificationButton pendingInvitesButton;
    private RealmsMainScreen.NotificationButton newsButton;
    private RealmsMainScreen.LayoutState activeLayoutState;
    @Nullable
    private HeaderAndFooterLayout layout;

    public RealmsMainScreen(Screen pLastScreen) {
        super(TITLE);
        this.lastScreen = pLastScreen;
        this.inviteNarrationLimiter = RateLimiter.create(0.016666668F);
    }

    @Override
    public void init() {
        this.serverList = new RealmsServerList(this.minecraft);
        this.realmSelectionList = new RealmsMainScreen.RealmSelectionList();
        Component component = Component.translatable("mco.invites.title");
        this.pendingInvitesButton = new RealmsMainScreen.NotificationButton(
            component, INVITE_SPRITE, p_296029_ -> this.minecraft.setScreen(new RealmsPendingInvitesScreen(this, component))
        );
        Component component1 = Component.translatable("mco.news");
        this.newsButton = new RealmsMainScreen.NotificationButton(component1, NEWS_SPRITE, p_296035_ -> {
            String s = this.newsLink;
            if (s != null) {
                ConfirmLinkScreen.confirmLinkNow(this, s);
                if (this.newsButton.notificationCount() != 0) {
                    RealmsPersistence.RealmsPersistenceData realmspersistence$realmspersistencedata = RealmsPersistence.readFile();
                    realmspersistence$realmspersistencedata.hasUnreadNews = false;
                    RealmsPersistence.writeFile(realmspersistence$realmspersistencedata);
                    this.newsButton.setNotificationCount(0);
                }
            }
        });
        this.newsButton.setTooltip(Tooltip.create(component1));
        this.playButton = Button.builder(PLAY_TEXT, p_86659_ -> play(this.getSelectedServer(), this)).width(100).build();
        this.configureButton = Button.builder(CONFIGURE_SERVER_TEXT, p_86672_ -> this.configureClicked(this.getSelectedServer())).width(100).build();
        this.renewButton = Button.builder(SUBSCRIPTION_RENEW_TEXT, p_86622_ -> this.onRenew(this.getSelectedServer())).width(100).build();
        this.leaveButton = Button.builder(LEAVE_SERVER_TEXT, p_86679_ -> this.leaveClicked(this.getSelectedServer())).width(100).build();
        this.addRealmButton = Button.builder(Component.translatable("mco.selectServer.purchase"), p_296032_ -> this.openTrialAvailablePopup()).size(100, 20).build();
        this.backButton = Button.builder(CommonComponents.GUI_BACK, p_325094_ -> this.onClose()).width(100).build();
        if (RealmsClient.ENVIRONMENT == RealmsClient.Environment.STAGE) {
            this.addRenderableWidget(
                CycleButton.booleanBuilder(Component.literal("Snapshot"), Component.literal("Release"))
                    .create(5, 5, 100, 20, Component.literal("Realm"), (p_308035_, p_308036_) -> {
                        f_303830_ = p_308036_;
                        this.f_302288_ = List.of();
                        this.m_305162_();
                    })
            );
        }

        this.updateLayout(RealmsMainScreen.LayoutState.LOADING);
        this.updateButtonStates();
        this.availability.thenAcceptAsync(p_296034_ -> {
            Screen screen = p_296034_.createErrorScreen(this.lastScreen);
            if (screen == null) {
                this.dataSubscription = this.initDataFetcher(this.minecraft.realmsDataFetcher());
            } else {
                this.minecraft.setScreen(screen);
            }
        }, this.screenExecutor);
    }

    public static boolean m_307170_() {
        return f_302464_ && f_303830_;
    }

    @Override
    protected void repositionElements() {
        if (this.layout != null) {
            this.realmSelectionList.m_319425_(this.width, this.layout);
            this.layout.arrangeElements();
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    private void m_304653_() {
        if (this.serverList.isEmpty() && this.f_302288_.isEmpty() && this.notifications.isEmpty()) {
            this.updateLayout(RealmsMainScreen.LayoutState.NO_REALMS);
        } else {
            this.updateLayout(RealmsMainScreen.LayoutState.LIST);
        }
    }

    private void updateLayout(RealmsMainScreen.LayoutState pLayoutState) {
        if (this.activeLayoutState != pLayoutState) {
            if (this.layout != null) {
                this.layout.visitWidgets(p_325098_ -> this.removeWidget(p_325098_));
            }

            this.layout = this.createLayout(pLayoutState);
            this.activeLayoutState = pLayoutState;
            this.layout.visitWidgets(p_325096_ -> {
                AbstractWidget abstractwidget = this.addRenderableWidget(p_325096_);
            });
            this.repositionElements();
        }
    }

    private HeaderAndFooterLayout createLayout(RealmsMainScreen.LayoutState pLayoutState) {
        HeaderAndFooterLayout headerandfooterlayout = new HeaderAndFooterLayout(this);
        headerandfooterlayout.setHeaderHeight(44);
        headerandfooterlayout.addToHeader(this.createHeader());
        Layout layout = this.createFooter(pLayoutState);
        layout.arrangeElements();
        headerandfooterlayout.setFooterHeight(layout.getHeight() + 22);
        headerandfooterlayout.addToFooter(layout);
        switch (pLayoutState) {
            case LOADING:
                headerandfooterlayout.addToContents(new LoadingDotsWidget(this.font, LOADING_TEXT));
                break;
            case NO_REALMS:
                headerandfooterlayout.addToContents(this.createNoRealmsContent());
                break;
            case LIST:
                headerandfooterlayout.addToContents(this.realmSelectionList);
        }

        return headerandfooterlayout;
    }

    private Layout createHeader() {
        int i = 90;
        LinearLayout linearlayout = LinearLayout.horizontal().spacing(4);
        linearlayout.defaultCellSetting().alignVerticallyMiddle();
        linearlayout.addChild(this.pendingInvitesButton);
        linearlayout.addChild(this.newsButton);
        LinearLayout linearlayout1 = LinearLayout.horizontal();
        linearlayout1.defaultCellSetting().alignVerticallyMiddle();
        linearlayout1.addChild(SpacerElement.width(90));
        linearlayout1.addChild(ImageWidget.texture(128, 34, LOGO_LOCATION, 128, 64), LayoutSettings::alignHorizontallyCenter);
        linearlayout1.addChild(new FrameLayout(90, 44)).addChild(linearlayout, LayoutSettings::alignHorizontallyRight);
        return linearlayout1;
    }

    private Layout createFooter(RealmsMainScreen.LayoutState pLayoutState) {
        GridLayout gridlayout = new GridLayout().spacing(4);
        GridLayout.RowHelper gridlayout$rowhelper = gridlayout.createRowHelper(3);
        if (pLayoutState == RealmsMainScreen.LayoutState.LIST) {
            gridlayout$rowhelper.addChild(this.playButton);
            gridlayout$rowhelper.addChild(this.configureButton);
            gridlayout$rowhelper.addChild(this.renewButton);
            gridlayout$rowhelper.addChild(this.leaveButton);
        }

        gridlayout$rowhelper.addChild(this.addRealmButton);
        gridlayout$rowhelper.addChild(this.backButton);
        return gridlayout;
    }

    private LinearLayout createNoRealmsContent() {
        LinearLayout linearlayout = LinearLayout.vertical().spacing(8);
        linearlayout.defaultCellSetting().alignHorizontallyCenter();
        linearlayout.addChild(ImageWidget.texture(130, 64, NO_REALMS_LOCATION, 130, 64));
        FocusableTextWidget focusabletextwidget = new FocusableTextWidget(308, NO_REALMS_TEXT, this.font, false, 4);
        linearlayout.addChild(focusabletextwidget);
        return linearlayout;
    }

    void updateButtonStates() {
        RealmsServer realmsserver = this.getSelectedServer();
        this.addRealmButton.active = this.activeLayoutState != RealmsMainScreen.LayoutState.LOADING;
        this.playButton.active = realmsserver != null && this.shouldPlayButtonBeActive(realmsserver);
        this.renewButton.active = realmsserver != null && this.shouldRenewButtonBeActive(realmsserver);
        this.leaveButton.active = realmsserver != null && this.shouldLeaveButtonBeActive(realmsserver);
        this.configureButton.active = realmsserver != null && this.shouldConfigureButtonBeActive(realmsserver);
    }

    boolean shouldPlayButtonBeActive(RealmsServer pRealmsServer) {
        boolean flag = !pRealmsServer.expired && pRealmsServer.state == RealmsServer.State.OPEN;
        return flag && (pRealmsServer.m_307151_() || this.isSelfOwnedServer(pRealmsServer));
    }

    private boolean shouldRenewButtonBeActive(RealmsServer pRealmsServer) {
        return pRealmsServer.expired && this.isSelfOwnedServer(pRealmsServer);
    }

    private boolean shouldConfigureButtonBeActive(RealmsServer pRealmsServer) {
        return this.isSelfOwnedServer(pRealmsServer) && pRealmsServer.state != RealmsServer.State.UNINITIALIZED;
    }

    private boolean shouldLeaveButtonBeActive(RealmsServer pRealmsServer) {
        return !this.isSelfOwnedServer(pRealmsServer);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.dataSubscription != null) {
            this.dataSubscription.tick();
        }
    }

    public static void refreshPendingInvites() {
        Minecraft.getInstance().realmsDataFetcher().pendingInvitesTask.reset();
    }

    public static void refreshServerList() {
        Minecraft.getInstance().realmsDataFetcher().serverListUpdateTask.reset();
    }

    private void m_305162_() {
        for (DataFetcher.Task<?> task : this.minecraft.realmsDataFetcher().m_307413_()) {
            task.reset();
        }
    }

    private DataFetcher.Subscription initDataFetcher(RealmsDataFetcher pDataFetcher) {
        DataFetcher.Subscription datafetcher$subscription = pDataFetcher.dataFetcher.createSubscription();
        datafetcher$subscription.subscribe(pDataFetcher.serverListUpdateTask, p_308037_ -> {
            this.serverList.updateServersList(p_308037_.f_302550_());
            this.f_302288_ = p_308037_.f_303266_();
            this.refreshRealmsSelectionList();
            boolean flag = false;

            for (RealmsServer realmsserver : this.serverList) {
                if (this.isSelfOwnedNonExpiredServer(realmsserver)) {
                    flag = true;
                }
            }

            if (!regionsPinged && flag) {
                regionsPinged = true;
                this.pingRegions();
            }
        });
        callRealmsClient(RealmsClient::getNotifications, p_274622_ -> {
            this.notifications.clear();
            this.notifications.addAll(p_274622_);

            for (RealmsNotification realmsnotification : p_274622_) {
                if (realmsnotification instanceof RealmsNotification.InfoPopup realmsnotification$infopopup) {
                    PopupScreen popupscreen = realmsnotification$infopopup.m_304898_(this, this::dismissNotification);
                    if (popupscreen != null) {
                        this.minecraft.setScreen(popupscreen);
                        this.m_305244_(List.of(realmsnotification));
                        break;
                    }
                }
            }

            if (!this.notifications.isEmpty() && this.activeLayoutState != RealmsMainScreen.LayoutState.LOADING) {
                this.refreshRealmsSelectionList();
            }
        });
        datafetcher$subscription.subscribe(pDataFetcher.pendingInvitesTask, p_296027_ -> {
            this.pendingInvitesButton.setNotificationCount(p_296027_);
            this.pendingInvitesButton.setTooltip(p_296027_ == 0 ? Tooltip.create(NO_PENDING_INVITES) : Tooltip.create(PENDING_INVITES));
            if (p_296027_ > 0 && this.inviteNarrationLimiter.tryAcquire(1)) {
                this.minecraft.getNarrator().sayNow(Component.translatable("mco.configure.world.invite.narration", p_296027_));
            }
        });
        datafetcher$subscription.subscribe(pDataFetcher.trialAvailabilityTask, p_296031_ -> this.trialsAvailable = p_296031_);
        datafetcher$subscription.subscribe(pDataFetcher.newsTask, p_296037_ -> {
            pDataFetcher.newsManager.updateUnreadNews(p_296037_);
            this.newsLink = pDataFetcher.newsManager.newsLink();
            this.newsButton.setNotificationCount(pDataFetcher.newsManager.hasUnreadNews() ? Integer.MAX_VALUE : 0);
        });
        return datafetcher$subscription;
    }

    private void m_305244_(Collection<RealmsNotification> p_311351_) {
        List<UUID> list = new ArrayList<>(p_311351_.size());

        for (RealmsNotification realmsnotification : p_311351_) {
            if (!realmsnotification.seen() && !this.handledSeenNotifications.contains(realmsnotification.uuid())) {
                list.add(realmsnotification.uuid());
            }
        }

        if (!list.isEmpty()) {
            callRealmsClient(p_274625_ -> {
                p_274625_.notificationsSeen(list);
                return null;
            }, p_274630_ -> this.handledSeenNotifications.addAll(list));
        }
    }

    private static <T> void callRealmsClient(RealmsMainScreen.RealmsCall<T> pCall, Consumer<T> pOnFinish) {
        Minecraft minecraft = Minecraft.getInstance();
        CompletableFuture.<T>supplyAsync(() -> {
            try {
                return pCall.request(RealmsClient.create(minecraft));
            } catch (RealmsServiceException realmsserviceexception) {
                throw new RuntimeException(realmsserviceexception);
            }
        }).thenAcceptAsync(pOnFinish, minecraft).exceptionally(p_274626_ -> {
            LOGGER.error("Failed to execute call to Realms Service", p_274626_);
            return null;
        });
    }

    private void refreshRealmsSelectionList() {
        RealmsServer realmsserver = this.getSelectedServer();
        this.realmSelectionList.clear();

        for (RealmsNotification realmsnotification : this.notifications) {
            if (this.m_307520_(realmsnotification)) {
                this.m_305244_(List.of(realmsnotification));
                break;
            }
        }

        for (RealmsServer realmsserver1 : this.f_302288_) {
            this.realmSelectionList.addEntry(new RealmsMainScreen.AvailableSnapshotEntry(realmsserver1));
        }

        for (RealmsServer realmsserver2 : this.serverList) {
            RealmsMainScreen.Entry realmsmainscreen$entry;
            if (m_307170_() && !realmsserver2.m_307276_()) {
                if (realmsserver2.state == RealmsServer.State.UNINITIALIZED) {
                    continue;
                }

                realmsmainscreen$entry = new RealmsMainScreen.ParentEntry(realmsserver2);
            } else {
                realmsmainscreen$entry = new RealmsMainScreen.ServerEntry(realmsserver2);
            }

            this.realmSelectionList.addEntry(realmsmainscreen$entry);
            if (realmsserver != null && realmsserver.id == realmsserver2.id) {
                this.realmSelectionList.setSelected(realmsmainscreen$entry);
            }
        }

        this.m_304653_();
        this.updateButtonStates();
    }

    private boolean m_307520_(RealmsNotification p_310789_) {
        if (!(p_310789_ instanceof RealmsNotification.VisitUrl realmsnotification$visiturl)) {
            return false;
        } else {
            Component component = realmsnotification$visiturl.getMessage();
            int i = this.font.wordWrapHeight(component, 216);
            int j = Mth.positiveCeilDiv(i + 7, 36) - 1;
            this.realmSelectionList.addEntry(new RealmsMainScreen.NotificationMessageEntry(component, j + 2, realmsnotification$visiturl));

            for (int k = 0; k < j; k++) {
                this.realmSelectionList.addEntry(new RealmsMainScreen.EmptyEntry());
            }

            this.realmSelectionList.addEntry(new RealmsMainScreen.ButtonEntry(realmsnotification$visiturl.buildOpenLinkButton(this)));
            return true;
        }
    }

    private void pingRegions() {
        new Thread(() -> {
            List<RegionPingResult> list = Ping.pingAllRegions();
            RealmsClient realmsclient = RealmsClient.create();
            PingResult pingresult = new PingResult();
            pingresult.pingResults = list;
            pingresult.f_317106_ = this.getOwnedNonExpiredWorldIds();

            try {
                realmsclient.sendPingResults(pingresult);
            } catch (Throwable throwable) {
                LOGGER.warn("Could not send ping result to Realms: ", throwable);
            }
        }).start();
    }

    private List<Long> getOwnedNonExpiredWorldIds() {
        List<Long> list = Lists.newArrayList();

        for (RealmsServer realmsserver : this.serverList) {
            if (this.isSelfOwnedNonExpiredServer(realmsserver)) {
                list.add(realmsserver.id);
            }
        }

        return list;
    }

    private void onRenew(@Nullable RealmsServer pRealmsServer) {
        if (pRealmsServer != null) {
            String s = CommonLinks.extendRealms(pRealmsServer.remoteSubscriptionId, this.minecraft.getUser().getProfileId(), pRealmsServer.expiredTrial);
            this.minecraft.keyboardHandler.setClipboard(s);
            Util.getPlatform().openUri(s);
        }
    }

    private void configureClicked(@Nullable RealmsServer pRealmsServer) {
        if (pRealmsServer != null && this.minecraft.isLocalPlayer(pRealmsServer.ownerUUID)) {
            this.minecraft.setScreen(new RealmsConfigureWorldScreen(this, pRealmsServer.id));
        }
    }

    private void leaveClicked(@Nullable RealmsServer pRealmsServer) {
        if (pRealmsServer != null && !this.minecraft.isLocalPlayer(pRealmsServer.ownerUUID)) {
            Component component = Component.translatable("mco.configure.world.leave.question.line1");
            Component component1 = Component.translatable("mco.configure.world.leave.question.line2");
            this.minecraft
                .setScreen(
                    new RealmsLongConfirmationScreen(
                        p_231253_ -> this.leaveServer(p_231253_, pRealmsServer), RealmsLongConfirmationScreen.Type.INFO, component, component1, true
                    )
                );
        }
    }

    @Nullable
    private RealmsServer getSelectedServer() {
        return this.realmSelectionList.getSelected() instanceof RealmsMainScreen.ServerEntry realmsmainscreen$serverentry ? realmsmainscreen$serverentry.getServer() : null;
    }

    private void leaveServer(boolean pConfirmed, final RealmsServer pServer) {
        if (pConfirmed) {
            (new Thread("Realms-leave-server") {
                    @Override
                    public void run() {
                        try {
                            RealmsClient realmsclient = RealmsClient.create();
                            realmsclient.uninviteMyselfFrom(pServer.id);
                            RealmsMainScreen.this.minecraft.execute(RealmsMainScreen::refreshServerList);
                        } catch (RealmsServiceException realmsserviceexception) {
                            RealmsMainScreen.LOGGER.error("Couldn't configure world", (Throwable)realmsserviceexception);
                            RealmsMainScreen.this.minecraft
                                .execute(
                                    () -> RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, RealmsMainScreen.this))
                                );
                        }
                    }
                })
                .start();
        }

        this.minecraft.setScreen(this);
    }

    void dismissNotification(UUID pUuid) {
        callRealmsClient(p_274628_ -> {
            p_274628_.notificationsDismiss(List.of(pUuid));
            return null;
        }, p_274632_ -> {
            this.notifications.removeIf(p_274621_ -> p_274621_.dismissable() && pUuid.equals(p_274621_.uuid()));
            this.refreshRealmsSelectionList();
        });
    }

    public void resetScreen() {
        this.realmSelectionList.setSelected(null);
        refreshServerList();
    }

    @Override
    public Component getNarrationMessage() {
        return (Component)(switch (this.activeLayoutState) {
            case LOADING -> CommonComponents.joinForNarration(super.getNarrationMessage(), LOADING_TEXT);
            case NO_REALMS -> CommonComponents.joinForNarration(super.getNarrationMessage(), NO_REALMS_TEXT);
            case LIST -> super.getNarrationMessage();
        });
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        if (m_307170_()) {
            pGuiGraphics.drawString(this.font, "Minecraft " + SharedConstants.getCurrentVersion().getName(), 2, this.height - 10, -1);
        }

        if (this.trialsAvailable && this.addRealmButton.active) {
            RealmsPopupScreen.renderDiamond(pGuiGraphics, this.addRealmButton);
        }

        switch (RealmsClient.ENVIRONMENT) {
            case STAGE:
                this.renderEnvironment(pGuiGraphics, "STAGE!", -256);
                break;
            case LOCAL:
                this.renderEnvironment(pGuiGraphics, "LOCAL!", 8388479);
        }
    }

    private void openTrialAvailablePopup() {
        this.minecraft.setScreen(new RealmsPopupScreen(this, this.trialsAvailable));
    }

    public static void play(@Nullable RealmsServer pRealmsServer, Screen pLastScreen) {
        m_307704_(pRealmsServer, pLastScreen, false);
    }

    public static void m_307704_(@Nullable RealmsServer p_312669_, Screen p_310591_, boolean p_309776_) {
        if (p_312669_ != null) {
            if (!m_307170_() || p_309776_) {
                Minecraft.getInstance().setScreen(new RealmsLongRunningMcoTaskScreen(p_310591_, new GetServerDetailsTask(p_310591_, p_312669_)));
                return;
            }

            switch (p_312669_.f_302826_) {
                case COMPATIBLE:
                    Minecraft.getInstance().setScreen(new RealmsLongRunningMcoTaskScreen(p_310591_, new GetServerDetailsTask(p_310591_, p_312669_)));
                    break;
                case UNVERIFIABLE:
                    m_306234_(
                        p_312669_,
                        p_310591_,
                        Component.translatable("mco.compatibility.unverifiable.title").m_306658_(-171),
                        Component.translatable("mco.compatibility.unverifiable.message"),
                        CommonComponents.GUI_CONTINUE
                    );
                    break;
                case NEEDS_DOWNGRADE:
                    m_306234_(
                        p_312669_,
                        p_310591_,
                        Component.translatable("selectWorld.backupQuestion.downgrade").m_306658_(-2142128),
                        Component.translatable(
                            "mco.compatibility.downgrade.description",
                            Component.literal(p_312669_.f_303415_).m_306658_(-171),
                            Component.literal(SharedConstants.getCurrentVersion().getName()).m_306658_(-171)
                        ),
                        Component.translatable("mco.compatibility.downgrade")
                    );
                    break;
                case NEEDS_UPGRADE:
                    m_306234_(
                        p_312669_,
                        p_310591_,
                        Component.translatable("mco.compatibility.upgrade.title").m_306658_(-171),
                        Component.translatable(
                            "mco.compatibility.upgrade.description",
                            Component.literal(p_312669_.f_303415_).m_306658_(-171),
                            Component.literal(SharedConstants.getCurrentVersion().getName()).m_306658_(-171)
                        ),
                        Component.translatable("mco.compatibility.upgrade")
                    );
            }
        }
    }

    private static void m_306234_(RealmsServer p_311893_, Screen p_310296_, Component p_309987_, Component p_309434_, Component p_311253_) {
        Minecraft.getInstance().setScreen(new ConfirmScreen(p_308034_ -> {
            Screen screen;
            if (p_308034_) {
                screen = new RealmsLongRunningMcoTaskScreen(p_310296_, new GetServerDetailsTask(p_310296_, p_311893_));
                refreshServerList();
            } else {
                screen = p_310296_;
            }

            Minecraft.getInstance().setScreen(screen);
        }, p_309987_, p_309434_, p_311253_, CommonComponents.GUI_CANCEL));
    }

    public static Component m_305395_(String p_312049_, boolean p_312280_) {
        return m_305180_(p_312049_, p_312280_ ? -8355712 : -2142128);
    }

    public static Component m_305180_(String p_311695_, int p_311083_) {
        return (Component)(StringUtils.isBlank(p_311695_)
            ? CommonComponents.EMPTY
            : Component.translatable("mco.version", Component.literal(p_311695_).m_306658_(p_311083_)));
    }

    boolean isSelfOwnedServer(RealmsServer pServer) {
        return this.minecraft.isLocalPlayer(pServer.ownerUUID);
    }

    private boolean isSelfOwnedNonExpiredServer(RealmsServer pServer) {
        return this.isSelfOwnedServer(pServer) && !pServer.expired;
    }

    private void renderEnvironment(GuiGraphics pGuiGraphics, String pText, int pColor) {
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate((float)(this.width / 2 - 25), 20.0F, 0.0F);
        pGuiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(-20.0F));
        pGuiGraphics.pose().scale(1.5F, 1.5F, 1.5F);
        pGuiGraphics.drawString(this.font, pText, 0, 0, pColor, false);
        pGuiGraphics.pose().popPose();
    }

    @OnlyIn(Dist.CLIENT)
    class AvailableSnapshotEntry extends RealmsMainScreen.Entry {
        private static final Component f_302194_ = Component.translatable("mco.snapshot.start");
        private static final int f_302397_ = 5;
        private final WidgetTooltipHolder f_302574_ = new WidgetTooltipHolder();
        private final RealmsServer f_302888_;

        public AvailableSnapshotEntry(final RealmsServer p_311559_) {
            this.f_302888_ = p_311559_;
            this.f_302574_.m_321872_(Tooltip.create(Component.translatable("mco.snapshot.tooltip")));
        }

        @Override
        public void render(
            GuiGraphics p_310547_,
            int p_310078_,
            int p_309934_,
            int p_311127_,
            int p_310500_,
            int p_311639_,
            int p_311442_,
            int p_309408_,
            boolean p_312327_,
            float p_309422_
        ) {
            p_310547_.blitSprite(RealmsMainScreen.NEW_REALM_SPRITE, p_311127_ - 5, p_309934_ + p_311639_ / 2 - 10, 40, 20);
            int i = p_309934_ + p_311639_ / 2 - 9 / 2;
            p_310547_.drawString(RealmsMainScreen.this.font, f_302194_, p_311127_ + 40 - 2, i - 5, 8388479);
            p_310547_.drawString(
                RealmsMainScreen.this.font, Component.translatable("mco.snapshot.description", this.f_302888_.name), p_311127_ + 40 - 2, i + 5, -8355712
            );
            this.f_302574_.m_323585_(p_312327_, this.isFocused(), new ScreenRectangle(p_311127_, p_309934_, p_310500_, p_311639_));
        }

        @Override
        public boolean mouseClicked(double p_310312_, double p_309519_, int p_313156_) {
            this.m_305349_();
            return true;
        }

        @Override
        public boolean keyPressed(int p_309531_, int p_310526_, int p_312670_) {
            if (CommonInputs.selected(p_309531_)) {
                this.m_305349_();
                return true;
            } else {
                return super.keyPressed(p_309531_, p_310526_, p_312670_);
            }
        }

        private void m_305349_() {
            RealmsMainScreen.this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            RealmsMainScreen.this.minecraft
                .setScreen(
                    new PopupScreen.Builder(RealmsMainScreen.this, Component.translatable("mco.snapshot.createSnapshotPopup.title"))
                        .m_307758_(Component.translatable("mco.snapshot.createSnapshotPopup.text"))
                        .m_305980_(
                            Component.translatable("mco.selectServer.create"),
                            p_325099_ -> RealmsMainScreen.this.minecraft.setScreen(new RealmsCreateRealmScreen(RealmsMainScreen.this, this.f_302888_.id))
                        )
                        .m_305980_(CommonComponents.GUI_CANCEL, PopupScreen::onClose)
                        .m_307029_()
                );
        }

        @Override
        public Component getNarration() {
            return Component.translatable(
                "gui.narrate.button", CommonComponents.joinForNarration(f_302194_, Component.translatable("mco.snapshot.description", this.f_302888_.name))
            );
        }
    }

    @OnlyIn(Dist.CLIENT)
    class ButtonEntry extends RealmsMainScreen.Entry {
        private final Button button;

        public ButtonEntry(final Button pButton) {
            this.button = pButton;
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            this.button.mouseClicked(pMouseX, pMouseY, pButton);
            return super.mouseClicked(pMouseX, pMouseY, pButton);
        }

        @Override
        public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
            return this.button.keyPressed(pKeyCode, pScanCode, pModifiers) ? true : super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }

        @Override
        public void render(
            GuiGraphics pGuiGraphics,
            int pIndex,
            int pTop,
            int pLeft,
            int pWidth,
            int pHeight,
            int pMouseX,
            int pMouseY,
            boolean pHovering,
            float pPartialTick
        ) {
            this.button.setPosition(RealmsMainScreen.this.width / 2 - 75, pTop + 4);
            this.button.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }

        @Override
        public void setFocused(boolean p_311570_) {
            super.setFocused(p_311570_);
            this.button.setFocused(p_311570_);
        }

        @Override
        public Component getNarration() {
            return this.button.getMessage();
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class CrossButton extends ImageButton {
        private static final WidgetSprites SPRITES = new WidgetSprites(
            new ResourceLocation("widget/cross_button"), new ResourceLocation("widget/cross_button_highlighted")
        );

        protected CrossButton(Button.OnPress pOnPress, Component pMessage) {
            super(0, 0, 14, 14, SPRITES, pOnPress);
            this.setTooltip(Tooltip.create(pMessage));
        }
    }

    @OnlyIn(Dist.CLIENT)
    class EmptyEntry extends RealmsMainScreen.Entry {
        @Override
        public void render(
            GuiGraphics p_301870_,
            int p_301858_,
            int p_301868_,
            int p_301866_,
            int p_301860_,
            int p_301859_,
            int p_301864_,
            int p_301865_,
            boolean p_301869_,
            float p_301861_
        ) {
        }

        @Override
        public Component getNarration() {
            return Component.empty();
        }
    }

    @OnlyIn(Dist.CLIENT)
    abstract class Entry extends ObjectSelectionList.Entry<RealmsMainScreen.Entry> {
        private static final int f_302391_ = 10;
        private static final int f_303651_ = 28;
        private static final int f_302724_ = 7;

        protected void m_306924_(RealmsServer p_312488_, GuiGraphics p_310620_, int p_309999_, int p_309772_, int p_310609_, int p_312927_) {
            int i = p_309999_ - 10 - 7;
            int j = p_309772_ + 2;
            if (p_312488_.expired) {
                this.m_305589_(p_310620_, i, j, p_310609_, p_312927_, RealmsMainScreen.EXPIRED_SPRITE, () -> RealmsMainScreen.SERVER_EXPIRED_TOOLTIP);
            } else if (p_312488_.state == RealmsServer.State.CLOSED) {
                this.m_305589_(p_310620_, i, j, p_310609_, p_312927_, RealmsMainScreen.CLOSED_SPRITE, () -> RealmsMainScreen.SERVER_CLOSED_TOOLTIP);
            } else if (RealmsMainScreen.this.isSelfOwnedServer(p_312488_) && p_312488_.daysLeft < 7) {
                this.m_305589_(
                    p_310620_,
                    i,
                    j,
                    p_310609_,
                    p_312927_,
                    RealmsMainScreen.EXPIRES_SOON_SPRITE,
                    () -> {
                        if (p_312488_.daysLeft <= 0) {
                            return RealmsMainScreen.SERVER_EXPIRES_SOON_TOOLTIP;
                        } else {
                            return (Component)(p_312488_.daysLeft == 1
                                ? RealmsMainScreen.SERVER_EXPIRES_IN_DAY_TOOLTIP
                                : Component.translatable("mco.selectServer.expires.days", p_312488_.daysLeft));
                        }
                    }
                );
            } else if (p_312488_.state == RealmsServer.State.OPEN) {
                this.m_305589_(p_310620_, i, j, p_310609_, p_312927_, RealmsMainScreen.OPEN_SPRITE, () -> RealmsMainScreen.SERVER_OPEN_TOOLTIP);
            }
        }

        private void m_305589_(
            GuiGraphics p_312727_, int p_311004_, int p_311259_, int p_310947_, int p_311421_, ResourceLocation p_313063_, Supplier<Component> p_312584_
        ) {
            p_312727_.blitSprite(p_313063_, p_311004_, p_311259_, 10, 28);
            if (RealmsMainScreen.this.realmSelectionList.isMouseOver((double)p_310947_, (double)p_311421_)
                && p_310947_ >= p_311004_
                && p_310947_ <= p_311004_ + 10
                && p_311421_ >= p_311259_
                && p_311421_ <= p_311259_ + 28) {
                RealmsMainScreen.this.setTooltipForNextRenderPass(p_312584_.get());
            }
        }

        protected void m_307830_(GuiGraphics p_309875_, int p_309431_, int p_312885_, RealmsServer p_311246_) {
            int i = this.m_306212_(p_312885_);
            int j = this.m_306180_(p_309431_);
            int k = this.m_306988_(j);
            if (!RealmsMainScreen.this.isSelfOwnedServer(p_311246_)) {
                p_309875_.drawString(RealmsMainScreen.this.font, p_311246_.owner, i, this.m_306988_(j), -8355712, false);
            } else if (p_311246_.expired) {
                Component component = p_311246_.expiredTrial ? RealmsMainScreen.TRIAL_EXPIRED_TEXT : RealmsMainScreen.SUBSCRIPTION_EXPIRED_TEXT;
                p_309875_.drawString(RealmsMainScreen.this.font, component, i, k, -2142128, false);
            }
        }

        protected void m_306296_(GuiGraphics p_311967_, String p_310470_, int p_311349_, int p_310646_, int p_312217_, int p_310447_) {
            int i = p_312217_ - p_311349_;
            if (RealmsMainScreen.this.font.width(p_310470_) > i) {
                String s = RealmsMainScreen.this.font.plainSubstrByWidth(p_310470_, i - RealmsMainScreen.this.font.width("... "));
                p_311967_.drawString(RealmsMainScreen.this.font, s + "...", p_311349_, p_310646_, p_310447_, false);
            } else {
                p_311967_.drawString(RealmsMainScreen.this.font, p_310470_, p_311349_, p_310646_, p_310447_, false);
            }
        }

        protected int m_306210_(int p_312234_, int p_313052_, Component p_311065_) {
            return p_312234_ + p_313052_ - RealmsMainScreen.this.font.width(p_311065_) - 20;
        }

        protected int m_306180_(int p_311005_) {
            return p_311005_ + 1;
        }

        protected int m_306182_() {
            return 2 + 9;
        }

        protected int m_306212_(int p_312460_) {
            return p_312460_ + 36 + 2;
        }

        protected int m_305425_(int p_309933_) {
            return p_309933_ + this.m_306182_();
        }

        protected int m_306988_(int p_310502_) {
            return p_310502_ + this.m_306182_() * 2;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static enum LayoutState {
        LOADING,
        NO_REALMS,
        LIST;
    }

    @OnlyIn(Dist.CLIENT)
    static class NotificationButton extends SpriteIconButton.CenteredIcon {
        private static final ResourceLocation[] NOTIFICATION_ICONS = new ResourceLocation[]{
            new ResourceLocation("notification/1"),
            new ResourceLocation("notification/2"),
            new ResourceLocation("notification/3"),
            new ResourceLocation("notification/4"),
            new ResourceLocation("notification/5"),
            new ResourceLocation("notification/more")
        };
        private static final int UNKNOWN_COUNT = Integer.MAX_VALUE;
        private static final int SIZE = 20;
        private static final int SPRITE_SIZE = 14;
        private int notificationCount;

        public NotificationButton(Component pMessage, ResourceLocation pSprite, Button.OnPress pOnPress) {
            super(20, 20, pMessage, 14, 14, pSprite, pOnPress, null);
        }

        int notificationCount() {
            return this.notificationCount;
        }

        public void setNotificationCount(int pNotificationCount) {
            this.notificationCount = pNotificationCount;
        }

        @Override
        public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            if (this.active && this.notificationCount != 0) {
                this.drawNotificationCounter(pGuiGraphics);
            }
        }

        private void drawNotificationCounter(GuiGraphics pGuiGraphics) {
            pGuiGraphics.blitSprite(NOTIFICATION_ICONS[Math.min(this.notificationCount, 6) - 1], this.getX() + this.getWidth() - 5, this.getY() - 3, 8, 8);
        }
    }

    @OnlyIn(Dist.CLIENT)
    class NotificationMessageEntry extends RealmsMainScreen.Entry {
        private static final int SIDE_MARGINS = 40;
        private static final int OUTLINE_COLOR = -12303292;
        private final Component text;
        private final int frameItemHeight;
        private final List<AbstractWidget> children = new ArrayList<>();
        @Nullable
        private final RealmsMainScreen.CrossButton dismissButton;
        private final MultiLineTextWidget textWidget;
        private final GridLayout gridLayout;
        private final FrameLayout textFrame;
        private int lastEntryWidth = -1;

        public NotificationMessageEntry(final Component pText, final int pFrameItemHeight, final RealmsNotification pNotification) {
            this.text = pText;
            this.frameItemHeight = pFrameItemHeight;
            this.gridLayout = new GridLayout();
            int i = 7;
            this.gridLayout.addChild(ImageWidget.sprite(20, 20, RealmsMainScreen.INFO_SPRITE), 0, 0, this.gridLayout.newCellSettings().padding(7, 7, 0, 0));
            this.gridLayout.addChild(SpacerElement.width(40), 0, 0);
            this.textFrame = this.gridLayout.addChild(new FrameLayout(0, 9 * 3 * (pFrameItemHeight - 1)), 0, 1, this.gridLayout.newCellSettings().paddingTop(7));
            this.textWidget = this.textFrame
                .addChild(
                    new MultiLineTextWidget(pText, RealmsMainScreen.this.font).setCentered(true), this.textFrame.newChildLayoutSettings().alignHorizontallyCenter().alignVerticallyTop()
                );
            this.gridLayout.addChild(SpacerElement.width(40), 0, 2);
            if (pNotification.dismissable()) {
                this.dismissButton = this.gridLayout
                    .addChild(
                        new RealmsMainScreen.CrossButton(
                            p_275478_ -> RealmsMainScreen.this.dismissNotification(pNotification.uuid()), Component.translatable("mco.notification.dismiss")
                        ),
                        0,
                        2,
                        this.gridLayout.newCellSettings().alignHorizontallyRight().padding(0, 7, 7, 0)
                    );
            } else {
                this.dismissButton = null;
            }

            this.gridLayout.visitWidgets(this.children::add);
        }

        @Override
        public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
            return this.dismissButton != null && this.dismissButton.keyPressed(pKeyCode, pScanCode, pModifiers) ? true : super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }

        private void updateEntryWidth(int pEntryWidth) {
            if (this.lastEntryWidth != pEntryWidth) {
                this.refreshLayout(pEntryWidth);
                this.lastEntryWidth = pEntryWidth;
            }
        }

        private void refreshLayout(int pWidth) {
            int i = pWidth - 80;
            this.textFrame.setMinWidth(i);
            this.textWidget.setMaxWidth(i);
            this.gridLayout.arrangeElements();
        }

        @Override
        public void renderBack(
            GuiGraphics pGuiGraphics,
            int pIndex,
            int pTop,
            int pLeft,
            int pWidth,
            int pHeight,
            int pMouseX,
            int pMouseY,
            boolean pIsMouseOver,
            float pPartialTick
        ) {
            super.renderBack(pGuiGraphics, pIndex, pTop, pLeft, pWidth, pHeight, pMouseX, pMouseY, pIsMouseOver, pPartialTick);
            pGuiGraphics.renderOutline(pLeft - 2, pTop - 2, pWidth, 36 * this.frameItemHeight - 2, -12303292);
        }

        @Override
        public void render(
            GuiGraphics pGuiGraphics,
            int pIndex,
            int pTop,
            int pLeft,
            int pWidth,
            int pHeight,
            int pMouseX,
            int pMouseY,
            boolean pHovering,
            float pPartialTick
        ) {
            this.gridLayout.setPosition(pLeft, pTop);
            this.updateEntryWidth(pWidth - 4);
            this.children.forEach(p_280688_ -> p_280688_.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick));
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            if (this.dismissButton != null) {
                this.dismissButton.mouseClicked(pMouseX, pMouseY, pButton);
            }

            return super.mouseClicked(pMouseX, pMouseY, pButton);
        }

        @Override
        public Component getNarration() {
            return this.text;
        }
    }

    @OnlyIn(Dist.CLIENT)
    class ParentEntry extends RealmsMainScreen.Entry {
        private final RealmsServer f_302672_;
        private final WidgetTooltipHolder f_302798_ = new WidgetTooltipHolder();

        public ParentEntry(final RealmsServer p_311143_) {
            this.f_302672_ = p_311143_;
            if (!p_311143_.expired) {
                this.f_302798_.m_321872_(Tooltip.create(Component.translatable("mco.snapshot.parent.tooltip")));
            }
        }

        @Override
        public boolean mouseClicked(double p_312445_, double p_311659_, int p_312397_) {
            return true;
        }

        @Override
        public void render(
            GuiGraphics p_312282_,
            int p_310045_,
            int p_311515_,
            int p_311448_,
            int p_310278_,
            int p_312055_,
            int p_311895_,
            int p_310535_,
            boolean p_312546_,
            float p_313200_
        ) {
            int i = this.m_306212_(p_311448_);
            int j = this.m_306180_(p_311515_);
            RealmsUtil.renderPlayerFace(p_312282_, p_311448_, p_311515_, 32, this.f_302672_.ownerUUID);
            Component component = RealmsMainScreen.m_305180_(this.f_302672_.f_303415_, -8355712);
            int k = this.m_306210_(p_311448_, p_310278_, component);
            this.m_306296_(p_312282_, this.f_302672_.getName(), i, j, k, -8355712);
            if (component != CommonComponents.EMPTY) {
                p_312282_.drawString(RealmsMainScreen.this.font, component, k, j, -8355712, false);
            }

            p_312282_.drawString(RealmsMainScreen.this.font, this.f_302672_.getDescription(), i, this.m_305425_(j), -8355712, false);
            this.m_307830_(p_312282_, p_311515_, p_311448_, this.f_302672_);
            this.m_306924_(this.f_302672_, p_312282_, p_311448_ + p_310278_, p_311515_, p_311895_, p_310535_);
            this.f_302798_.m_323585_(p_312546_, this.isFocused(), new ScreenRectangle(p_311448_, p_311515_, p_310278_, p_312055_));
        }

        @Override
        public Component getNarration() {
            return Component.literal(this.f_302672_.name);
        }
    }

    @OnlyIn(Dist.CLIENT)
    class RealmSelectionList extends RealmsObjectSelectionList<RealmsMainScreen.Entry> {
        public RealmSelectionList() {
            super(RealmsMainScreen.this.width, RealmsMainScreen.this.height, 0, 36);
        }

        public void setSelected(@Nullable RealmsMainScreen.Entry p_86849_) {
            super.setSelected(p_86849_);
            RealmsMainScreen.this.updateButtonStates();
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 36;
        }

        @Override
        public int getRowWidth() {
            return 300;
        }
    }

    @OnlyIn(Dist.CLIENT)
    interface RealmsCall<T> {
        T request(RealmsClient pRealmsClient) throws RealmsServiceException;
    }

    @OnlyIn(Dist.CLIENT)
    class ServerEntry extends RealmsMainScreen.Entry {
        private static final int SKIN_HEAD_LARGE_WIDTH = 36;
        private final RealmsServer serverData;
        private final WidgetTooltipHolder f_302281_ = new WidgetTooltipHolder();

        public ServerEntry(final RealmsServer pServerData) {
            this.serverData = pServerData;
            boolean flag = RealmsMainScreen.this.isSelfOwnedServer(pServerData);
            if (RealmsMainScreen.m_307170_() && flag && pServerData.m_307276_()) {
                this.f_302281_.m_321872_(Tooltip.create(Component.translatable("mco.snapshot.paired", pServerData.f_302572_)));
            } else if (!flag && pServerData.m_306853_()) {
                this.f_302281_.m_321872_(Tooltip.create(Component.translatable("mco.snapshot.friendsRealm.upgrade", pServerData.owner)));
            } else if (!flag && pServerData.m_307341_()) {
                this.f_302281_.m_321872_(Tooltip.create(Component.translatable("mco.snapshot.friendsRealm.downgrade", pServerData.f_303415_)));
            }
        }

        @Override
        public void render(
            GuiGraphics pGuiGraphics,
            int pIndex,
            int pTop,
            int pLeft,
            int pWidth,
            int pHeight,
            int pMouseX,
            int pMouseY,
            boolean pHovering,
            float pPartialTick
        ) {
            if (this.serverData.state == RealmsServer.State.UNINITIALIZED) {
                pGuiGraphics.blitSprite(RealmsMainScreen.NEW_REALM_SPRITE, pLeft - 5, pTop + pHeight / 2 - 10, 40, 20);
                int i = pTop + pHeight / 2 - 9 / 2;
                pGuiGraphics.drawString(RealmsMainScreen.this.font, RealmsMainScreen.SERVER_UNITIALIZED_TEXT, pLeft + 40 - 2, i, 8388479);
            } else {
                RealmsUtil.renderPlayerFace(pGuiGraphics, pLeft, pTop, 32, this.serverData.ownerUUID);
                this.m_307295_(pGuiGraphics, pTop, pLeft, pWidth);
                this.m_304906_(pGuiGraphics, pTop, pLeft);
                this.m_307830_(pGuiGraphics, pTop, pLeft, this.serverData);
                this.m_306924_(this.serverData, pGuiGraphics, pLeft + pWidth, pTop, pMouseX, pMouseY);
                this.f_302281_.m_323585_(pHovering, this.isFocused(), new ScreenRectangle(pLeft, pTop, pWidth, pHeight));
            }
        }

        private void m_307295_(GuiGraphics p_311326_, int p_311522_, int p_312647_, int p_310935_) {
            int i = this.m_306212_(p_312647_);
            int j = this.m_306180_(p_311522_);
            Component component = RealmsMainScreen.m_305395_(this.serverData.f_303415_, this.serverData.m_307151_());
            int k = this.m_306210_(p_312647_, p_310935_, component);
            this.m_306296_(p_311326_, this.serverData.getName(), i, j, k, -1);
            if (component != CommonComponents.EMPTY) {
                p_311326_.drawString(RealmsMainScreen.this.font, component, k, j, -8355712, false);
            }
        }

        private void m_304906_(GuiGraphics p_311635_, int p_311064_, int p_310167_) {
            int i = this.m_306212_(p_310167_);
            int j = this.m_306180_(p_311064_);
            int k = this.m_305425_(j);
            String s = this.serverData.getMinigameName();
            if (this.serverData.worldType == RealmsServer.WorldType.MINIGAME && s != null) {
                Component component = Component.literal(s).withStyle(ChatFormatting.GRAY);
                p_311635_.drawString(
                    RealmsMainScreen.this.font, Component.translatable("mco.selectServer.minigameName", component).m_306658_(-171), i, k, -1, false
                );
            } else {
                p_311635_.drawString(RealmsMainScreen.this.font, this.serverData.getDescription(), i, this.m_305425_(j), -8355712, false);
            }
        }

        private void playRealm() {
            RealmsMainScreen.this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            RealmsMainScreen.play(this.serverData, RealmsMainScreen.this);
        }

        private void createUnitializedRealm() {
            RealmsMainScreen.this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            RealmsCreateRealmScreen realmscreaterealmscreen = new RealmsCreateRealmScreen(RealmsMainScreen.this, this.serverData);
            RealmsMainScreen.this.minecraft.setScreen(realmscreaterealmscreen);
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            if (this.serverData.state == RealmsServer.State.UNINITIALIZED) {
                this.createUnitializedRealm();
            } else if (RealmsMainScreen.this.shouldPlayButtonBeActive(this.serverData)) {
                if (Util.getMillis() - RealmsMainScreen.this.lastClickTime < 250L && this.isFocused()) {
                    this.playRealm();
                }

                RealmsMainScreen.this.lastClickTime = Util.getMillis();
            }

            return true;
        }

        @Override
        public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
            if (CommonInputs.selected(pKeyCode)) {
                if (this.serverData.state == RealmsServer.State.UNINITIALIZED) {
                    this.createUnitializedRealm();
                    return true;
                }

                if (RealmsMainScreen.this.shouldPlayButtonBeActive(this.serverData)) {
                    this.playRealm();
                    return true;
                }
            }

            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }

        @Override
        public Component getNarration() {
            return (Component)(this.serverData.state == RealmsServer.State.UNINITIALIZED
                ? RealmsMainScreen.UNITIALIZED_WORLD_NARRATION
                : Component.translatable("narrator.select", this.serverData.name));
        }

        public RealmsServer getServer() {
            return this.serverData;
        }
    }
}
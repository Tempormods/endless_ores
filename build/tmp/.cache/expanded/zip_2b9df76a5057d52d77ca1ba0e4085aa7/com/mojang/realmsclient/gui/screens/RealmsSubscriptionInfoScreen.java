package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.annotation.Nullable;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FittingMultiLineTextWidget;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.CommonLinks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsSubscriptionInfoScreen extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Component SUBSCRIPTION_TITLE = Component.translatable("mco.configure.world.subscription.title");
    private static final Component SUBSCRIPTION_START_LABEL = Component.translatable("mco.configure.world.subscription.start");
    private static final Component TIME_LEFT_LABEL = Component.translatable("mco.configure.world.subscription.timeleft");
    private static final Component DAYS_LEFT_LABEL = Component.translatable("mco.configure.world.subscription.recurring.daysleft");
    private static final Component SUBSCRIPTION_EXPIRED_TEXT = Component.translatable("mco.configure.world.subscription.expired");
    private static final Component SUBSCRIPTION_LESS_THAN_A_DAY_TEXT = Component.translatable("mco.configure.world.subscription.less_than_a_day");
    private static final Component UNKNOWN = Component.translatable("mco.configure.world.subscription.unknown");
    private static final Component RECURRING_INFO = Component.translatable("mco.configure.world.subscription.recurring.info");
    private final Screen lastScreen;
    final RealmsServer serverData;
    final Screen mainScreen;
    private Component daysLeft = UNKNOWN;
    private Component startDate = UNKNOWN;
    @Nullable
    private Subscription.SubscriptionType type;

    public RealmsSubscriptionInfoScreen(Screen pLastScreen, RealmsServer pServerData, Screen pMainScreen) {
        super(GameNarrator.NO_TITLE);
        this.lastScreen = pLastScreen;
        this.serverData = pServerData;
        this.mainScreen = pMainScreen;
    }

    @Override
    public void init() {
        this.getSubscription(this.serverData.id);
        this.addRenderableWidget(
            Button.builder(
                    Component.translatable("mco.configure.world.subscription.extend"),
                    p_296090_ -> ConfirmLinkScreen.confirmLinkNow(this, CommonLinks.extendRealms(this.serverData.remoteSubscriptionId, this.minecraft.getUser().getProfileId()))
                )
                .bounds(this.width / 2 - 100, row(6), 200, 20)
                .build()
        );
        if (this.serverData.expired) {
            this.addRenderableWidget(
                Button.builder(
                        Component.translatable("mco.configure.world.delete.button"),
                        p_287304_ -> {
                            Component component = Component.translatable("mco.configure.world.delete.question.line1");
                            Component component1 = Component.translatable("mco.configure.world.delete.question.line2");
                            this.minecraft
                                .setScreen(
                                    new RealmsLongConfirmationScreen(this::deleteRealm, RealmsLongConfirmationScreen.Type.WARNING, component, component1, true)
                                );
                        }
                    )
                    .bounds(this.width / 2 - 100, row(10), 200, 20)
                    .build()
            );
        } else if (RealmsMainScreen.m_307170_() && this.serverData.f_302572_ != null) {
            this.addRenderableWidget(
                new FittingMultiLineTextWidget(
                        this.width / 2 - 100,
                        row(8),
                        200,
                        46,
                        Component.translatable("mco.snapshot.subscription.info", this.serverData.f_302572_),
                        this.font
                    )
                    .setColor(-6250336)
            );
        } else {
            this.addRenderableWidget(new FittingMultiLineTextWidget(this.width / 2 - 100, row(8), 200, 46, RECURRING_INFO, this.font).setColor(-6250336));
        }

        this.addRenderableWidget(
            Button.builder(CommonComponents.GUI_BACK, p_280760_ -> this.onClose()).bounds(this.width / 2 - 100, row(12), 200, 20).build()
        );
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinLines(SUBSCRIPTION_TITLE, SUBSCRIPTION_START_LABEL, this.startDate, TIME_LEFT_LABEL, this.daysLeft);
    }

    private void deleteRealm(boolean p_90012_) {
        if (p_90012_) {
            (new Thread("Realms-delete-realm") {
                    @Override
                    public void run() {
                        try {
                            RealmsClient realmsclient = RealmsClient.create();
                            realmsclient.deleteWorld(RealmsSubscriptionInfoScreen.this.serverData.id);
                        } catch (RealmsServiceException realmsserviceexception) {
                            RealmsSubscriptionInfoScreen.LOGGER.error("Couldn't delete world", (Throwable)realmsserviceexception);
                        }

                        RealmsSubscriptionInfoScreen.this.minecraft
                            .execute(() -> RealmsSubscriptionInfoScreen.this.minecraft.setScreen(RealmsSubscriptionInfoScreen.this.mainScreen));
                    }
                })
                .start();
        }

        this.minecraft.setScreen(this);
    }

    private void getSubscription(long pServerId) {
        RealmsClient realmsclient = RealmsClient.create();

        try {
            Subscription subscription = realmsclient.subscriptionFor(pServerId);
            this.daysLeft = this.daysLeftPresentation(subscription.daysLeft);
            this.startDate = localPresentation(subscription.startDate);
            this.type = subscription.type;
        } catch (RealmsServiceException realmsserviceexception) {
            LOGGER.error("Couldn't get subscription", (Throwable)realmsserviceexception);
            this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, this.lastScreen));
        }
    }

    private static Component localPresentation(long pTime) {
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.setTimeInMillis(pTime);
        return Component.literal(DateFormat.getDateTimeInstance().format(calendar.getTime()));
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        int i = this.width / 2 - 100;
        pGuiGraphics.drawCenteredString(this.font, SUBSCRIPTION_TITLE, this.width / 2, 17, -1);
        pGuiGraphics.drawString(this.font, SUBSCRIPTION_START_LABEL, i, row(0), -6250336, false);
        pGuiGraphics.drawString(this.font, this.startDate, i, row(1), -1, false);
        if (this.type == Subscription.SubscriptionType.NORMAL) {
            pGuiGraphics.drawString(this.font, TIME_LEFT_LABEL, i, row(3), -6250336, false);
        } else if (this.type == Subscription.SubscriptionType.RECURRING) {
            pGuiGraphics.drawString(this.font, DAYS_LEFT_LABEL, i, row(3), -6250336, false);
        }

        pGuiGraphics.drawString(this.font, this.daysLeft, i, row(4), -1, false);
    }

    private Component daysLeftPresentation(int pDaysLeft) {
        if (pDaysLeft < 0 && this.serverData.expired) {
            return SUBSCRIPTION_EXPIRED_TEXT;
        } else if (pDaysLeft <= 1) {
            return SUBSCRIPTION_LESS_THAN_A_DAY_TEXT;
        } else {
            int i = pDaysLeft / 30;
            int j = pDaysLeft % 30;
            boolean flag = i > 0;
            boolean flag1 = j > 0;
            if (flag && flag1) {
                return Component.translatable("mco.configure.world.subscription.remaining.months.days", i, j);
            } else if (flag) {
                return Component.translatable("mco.configure.world.subscription.remaining.months", i);
            } else {
                return flag1 ? Component.translatable("mco.configure.world.subscription.remaining.days", j) : Component.empty();
            }
        }
    }
}
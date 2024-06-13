package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.WorldGenerationInfo;
import com.mojang.realmsclient.util.task.LongRunningTask;
import com.mojang.realmsclient.util.task.RealmCreationTask;
import com.mojang.realmsclient.util.task.ResettingGeneratedWorldTask;
import com.mojang.realmsclient.util.task.ResettingTemplateWorldTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsResetWorldScreen extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Component CREATE_REALM_TITLE = Component.translatable("mco.selectServer.create");
    private static final Component CREATE_REALM_SUBTITLE = Component.translatable("mco.selectServer.create.subtitle");
    private static final Component CREATE_WORLD_TITLE = Component.translatable("mco.configure.world.switch.slot");
    private static final Component CREATE_WORLD_SUBTITLE = Component.translatable("mco.configure.world.switch.slot.subtitle");
    private static final Component RESET_WORLD_TITLE = Component.translatable("mco.reset.world.title");
    private static final Component RESET_WORLD_SUBTITLE = Component.translatable("mco.reset.world.warning");
    public static final Component CREATE_WORLD_RESET_TASK_TITLE = Component.translatable("mco.create.world.reset.title");
    private static final Component RESET_WORLD_RESET_TASK_TITLE = Component.translatable("mco.reset.world.resetting.screen.title");
    private static final Component WORLD_TEMPLATES_TITLE = Component.translatable("mco.reset.world.template");
    private static final Component ADVENTURES_TITLE = Component.translatable("mco.reset.world.adventure");
    private static final Component EXPERIENCES_TITLE = Component.translatable("mco.reset.world.experience");
    private static final Component INSPIRATION_TITLE = Component.translatable("mco.reset.world.inspiration");
    private final Screen lastScreen;
    private final RealmsServer serverData;
    private final Component subtitle;
    private final int subtitleColor;
    private final Component resetTaskTitle;
    private static final ResourceLocation UPLOAD_LOCATION = new ResourceLocation("textures/gui/realms/upload.png");
    private static final ResourceLocation ADVENTURE_MAP_LOCATION = new ResourceLocation("textures/gui/realms/adventure.png");
    private static final ResourceLocation SURVIVAL_SPAWN_LOCATION = new ResourceLocation("textures/gui/realms/survival_spawn.png");
    private static final ResourceLocation NEW_WORLD_LOCATION = new ResourceLocation("textures/gui/realms/new_world.png");
    private static final ResourceLocation EXPERIENCE_LOCATION = new ResourceLocation("textures/gui/realms/experience.png");
    private static final ResourceLocation INSPIRATION_LOCATION = new ResourceLocation("textures/gui/realms/inspiration.png");
    WorldTemplatePaginatedList templates;
    WorldTemplatePaginatedList adventuremaps;
    WorldTemplatePaginatedList experiences;
    WorldTemplatePaginatedList inspirations;
    public final int slot;
    @Nullable
    private final RealmCreationTask f_314298_;
    private final Runnable resetWorldRunnable;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

    private RealmsResetWorldScreen(
        Screen pLastScreen, RealmsServer pServerData, int pSlot, Component pTitle, Component pSubtitle, int pSubtitleColor, Component pResetTaskTitle, Runnable pResetWorldRunnable
    ) {
        this(pLastScreen, pServerData, pSlot, pTitle, pSubtitle, pSubtitleColor, pResetTaskTitle, null, pResetWorldRunnable);
    }

    public RealmsResetWorldScreen(
        Screen p_310390_,
        RealmsServer p_311752_,
        int p_312610_,
        Component p_310665_,
        Component p_312883_,
        int p_311717_,
        Component p_309644_,
        @Nullable RealmCreationTask p_331078_,
        Runnable p_311131_
    ) {
        super(p_310665_);
        this.lastScreen = p_310390_;
        this.serverData = p_311752_;
        this.slot = p_312610_;
        this.subtitle = p_312883_;
        this.subtitleColor = p_311717_;
        this.resetTaskTitle = p_309644_;
        this.f_314298_ = p_331078_;
        this.resetWorldRunnable = p_311131_;
    }

    public static RealmsResetWorldScreen forNewRealm(Screen pLastScreen, RealmsServer pServerData, RealmCreationTask p_333820_, Runnable pResetWorldRunnable) {
        return new RealmsResetWorldScreen(pLastScreen, pServerData, pServerData.activeSlot, CREATE_REALM_TITLE, CREATE_REALM_SUBTITLE, -6250336, CREATE_WORLD_RESET_TASK_TITLE, p_333820_, pResetWorldRunnable);
    }

    public static RealmsResetWorldScreen forEmptySlot(Screen pLastScreen, int pSlot, RealmsServer pServerData, Runnable pResetWorldRunnable) {
        return new RealmsResetWorldScreen(pLastScreen, pServerData, pSlot, CREATE_WORLD_TITLE, CREATE_WORLD_SUBTITLE, -6250336, CREATE_WORLD_RESET_TASK_TITLE, pResetWorldRunnable);
    }

    public static RealmsResetWorldScreen forResetSlot(Screen pLastScreen, RealmsServer pServerData, Runnable pResetWorldRunnable) {
        return new RealmsResetWorldScreen(pLastScreen, pServerData, pServerData.activeSlot, RESET_WORLD_TITLE, RESET_WORLD_SUBTITLE, -65536, RESET_WORLD_RESET_TASK_TITLE, pResetWorldRunnable);
    }

    @Override
    public void init() {
        LinearLayout linearlayout = this.layout.addToHeader(LinearLayout.vertical());
        linearlayout.defaultCellSetting().padding(9 / 3);
        linearlayout.addChild(new StringWidget(this.title, this.font), LayoutSettings::alignHorizontallyCenter);
        linearlayout.addChild(new StringWidget(this.subtitle, this.font).setColor(this.subtitleColor), LayoutSettings::alignHorizontallyCenter);
        (new Thread("Realms-reset-world-fetcher") {
            @Override
            public void run() {
                RealmsClient realmsclient = RealmsClient.create();

                try {
                    WorldTemplatePaginatedList worldtemplatepaginatedlist = realmsclient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.NORMAL);
                    WorldTemplatePaginatedList worldtemplatepaginatedlist1 = realmsclient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.ADVENTUREMAP);
                    WorldTemplatePaginatedList worldtemplatepaginatedlist2 = realmsclient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.EXPERIENCE);
                    WorldTemplatePaginatedList worldtemplatepaginatedlist3 = realmsclient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.INSPIRATION);
                    RealmsResetWorldScreen.this.minecraft.execute(() -> {
                        RealmsResetWorldScreen.this.templates = worldtemplatepaginatedlist;
                        RealmsResetWorldScreen.this.adventuremaps = worldtemplatepaginatedlist1;
                        RealmsResetWorldScreen.this.experiences = worldtemplatepaginatedlist2;
                        RealmsResetWorldScreen.this.inspirations = worldtemplatepaginatedlist3;
                    });
                } catch (RealmsServiceException realmsserviceexception) {
                    RealmsResetWorldScreen.LOGGER.error("Couldn't fetch templates in reset world", (Throwable)realmsserviceexception);
                }
            }
        }).start();
        GridLayout gridlayout = this.layout.addToContents(new GridLayout());
        GridLayout.RowHelper gridlayout$rowhelper = gridlayout.createRowHelper(3);
        gridlayout$rowhelper.defaultCellSetting().paddingHorizontal(16);
        gridlayout$rowhelper.addChild(
            new RealmsResetWorldScreen.FrameButton(
                this.minecraft.font,
                RealmsResetNormalWorldScreen.TITLE,
                NEW_WORLD_LOCATION,
                p_280746_ -> this.minecraft.setScreen(new RealmsResetNormalWorldScreen(this::generationSelectionCallback, this.title))
            )
        );
        gridlayout$rowhelper.addChild(
            new RealmsResetWorldScreen.FrameButton(
                this.minecraft.font,
                RealmsSelectFileToUploadScreen.TITLE,
                UPLOAD_LOCATION,
                p_325157_ -> this.minecraft.setScreen(new RealmsSelectFileToUploadScreen(this.f_314298_, this.serverData.id, this.slot, this))
            )
        );
        gridlayout$rowhelper.addChild(
            new RealmsResetWorldScreen.FrameButton(
                this.minecraft.font,
                WORLD_TEMPLATES_TITLE,
                SURVIVAL_SPAWN_LOCATION,
                p_296082_ -> this.minecraft
                        .setScreen(new RealmsSelectWorldTemplateScreen(WORLD_TEMPLATES_TITLE, this::templateSelectionCallback, RealmsServer.WorldType.NORMAL, this.templates))
            )
        );
        gridlayout$rowhelper.addChild(SpacerElement.height(16), 3);
        gridlayout$rowhelper.addChild(
            new RealmsResetWorldScreen.FrameButton(
                this.minecraft.font,
                ADVENTURES_TITLE,
                ADVENTURE_MAP_LOCATION,
                p_296077_ -> this.minecraft
                        .setScreen(new RealmsSelectWorldTemplateScreen(ADVENTURES_TITLE, this::templateSelectionCallback, RealmsServer.WorldType.ADVENTUREMAP, this.adventuremaps))
            )
        );
        gridlayout$rowhelper.addChild(
            new RealmsResetWorldScreen.FrameButton(
                this.minecraft.font,
                EXPERIENCES_TITLE,
                EXPERIENCE_LOCATION,
                p_296080_ -> this.minecraft
                        .setScreen(new RealmsSelectWorldTemplateScreen(EXPERIENCES_TITLE, this::templateSelectionCallback, RealmsServer.WorldType.EXPERIENCE, this.experiences))
            )
        );
        gridlayout$rowhelper.addChild(
            new RealmsResetWorldScreen.FrameButton(
                this.minecraft.font,
                INSPIRATION_TITLE,
                INSPIRATION_LOCATION,
                p_296083_ -> this.minecraft
                        .setScreen(new RealmsSelectWorldTemplateScreen(INSPIRATION_TITLE, this::templateSelectionCallback, RealmsServer.WorldType.INSPIRATION, this.inspirations))
            )
        );
        this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, p_296085_ -> this.onClose()).build());
        this.layout.visitWidgets(p_325156_ -> {
            AbstractWidget abstractwidget = this.addRenderableWidget(p_325156_);
        });
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(this.getTitle(), this.subtitle);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    private void templateSelectionCallback(@Nullable WorldTemplate p_167454_) {
        this.minecraft.setScreen(this);
        if (p_167454_ != null) {
            this.m_306545_(new ResettingTemplateWorldTask(p_167454_, this.serverData.id, this.resetTaskTitle, this.resetWorldRunnable));
        }
    }

    private void generationSelectionCallback(@Nullable WorldGenerationInfo p_167456_) {
        this.minecraft.setScreen(this);
        if (p_167456_ != null) {
            this.m_306545_(new ResettingGeneratedWorldTask(p_167456_, this.serverData.id, this.resetTaskTitle, this.resetWorldRunnable));
        }
    }

    private void m_306545_(LongRunningTask p_311937_) {
        List<LongRunningTask> list = new ArrayList<>();
        if (this.f_314298_ != null) {
            list.add(this.f_314298_);
        }

        if (this.slot != this.serverData.activeSlot) {
            list.add(new SwitchSlotTask(this.serverData.id, this.slot, () -> {
            }));
        }

        list.add(p_311937_);
        this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, list.toArray(new LongRunningTask[0])));
    }

    @OnlyIn(Dist.CLIENT)
    class FrameButton extends Button {
        private static final ResourceLocation f_316773_ = new ResourceLocation("widget/slot_frame");
        private static final int f_316774_ = 60;
        private static final int f_315122_ = 2;
        private static final int IMAGE_SIZE = 56;
        private final ResourceLocation image;

        FrameButton(final Font p_328407_, final Component pMessage, final ResourceLocation pImage, final Button.OnPress pOnPress) {
            super(0, 0, 60, 60 + 9, pMessage, pOnPress, DEFAULT_NARRATION);
            this.image = pImage;
        }

        @Override
        public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            boolean flag = this.isHoveredOrFocused();
            if (flag) {
                pGuiGraphics.setColor(0.56F, 0.56F, 0.56F, 1.0F);
            }

            int i = this.getX();
            int j = this.getY();
            pGuiGraphics.blit(this.image, i + 2, j + 2, 0.0F, 0.0F, 56, 56, 56, 56);
            pGuiGraphics.blitSprite(f_316773_, i, j, 60, 60);
            pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            int k = flag ? -6250336 : -1;
            pGuiGraphics.drawCenteredString(RealmsResetWorldScreen.this.font, this.getMessage(), i + 28, j - 14, k);
        }
    }
}
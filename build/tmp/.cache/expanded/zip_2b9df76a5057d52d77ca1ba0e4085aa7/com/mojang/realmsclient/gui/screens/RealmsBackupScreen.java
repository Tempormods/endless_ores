package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.task.DownloadTask;
import com.mojang.realmsclient.util.task.RestoreTask;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsBackupScreen extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Component TITLE = Component.translatable("mco.configure.world.backup");
    static final Component RESTORE_TOOLTIP = Component.translatable("mco.backup.button.restore");
    static final Component HAS_CHANGES_TOOLTIP = Component.translatable("mco.backup.changes.tooltip");
    private static final Component NO_BACKUPS_LABEL = Component.translatable("mco.backup.nobackups");
    private static final String UPLOADED_KEY = "uploaded";
    private static final int f_314703_ = 8;
    final RealmsConfigureWorldScreen lastScreen;
    List<Backup> backups = Collections.emptyList();
    @Nullable
    RealmsBackupScreen.BackupObjectSelectionList f_314156_;
    final HeaderAndFooterLayout f_314075_ = new HeaderAndFooterLayout(this);
    private final int slotId;
    @Nullable
    Button downloadButton;
    final RealmsServer serverData;
    boolean noBackups = false;

    public RealmsBackupScreen(RealmsConfigureWorldScreen pLastScreen, RealmsServer pServerData, int pSlotId) {
        super(TITLE);
        this.lastScreen = pLastScreen;
        this.serverData = pServerData;
        this.slotId = pSlotId;
    }

    @Override
    public void init() {
        this.f_314075_.m_324480_(TITLE, this.font);
        this.f_314156_ = this.f_314075_.addToContents(new RealmsBackupScreen.BackupObjectSelectionList());
        LinearLayout linearlayout = this.f_314075_.addToFooter(LinearLayout.horizontal().spacing(8));
        this.downloadButton = linearlayout.addChild(Button.builder(Component.translatable("mco.backup.button.download"), p_88185_ -> this.downloadClicked()).build());
        this.downloadButton.active = false;
        linearlayout.addChild(Button.builder(CommonComponents.GUI_BACK, p_325106_ -> this.onClose()).build());
        this.f_314075_.visitWidgets(p_325105_ -> {
            AbstractWidget abstractwidget = this.addRenderableWidget(p_325105_);
        });
        this.repositionElements();
        this.m_321557_();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        if (this.noBackups && this.f_314156_ != null) {
            pGuiGraphics.drawString(
                this.font,
                NO_BACKUPS_LABEL,
                this.width / 2 - this.font.width(NO_BACKUPS_LABEL) / 2,
                this.f_314156_.getY() + this.f_314156_.getHeight() / 2 - 9 / 2,
                -1,
                false
            );
        }
    }

    @Override
    protected void repositionElements() {
        this.f_314075_.arrangeElements();
        if (this.f_314156_ != null) {
            this.f_314156_.m_319425_(this.width, this.f_314075_);
        }
    }

    private void m_321557_() {
        (new Thread("Realms-fetch-backups") {
            @Override
            public void run() {
                RealmsClient realmsclient = RealmsClient.create();

                try {
                    List<Backup> list = realmsclient.backupsFor(RealmsBackupScreen.this.serverData.id).backups;
                    RealmsBackupScreen.this.minecraft.execute(() -> {
                        RealmsBackupScreen.this.backups = list;
                        RealmsBackupScreen.this.noBackups = RealmsBackupScreen.this.backups.isEmpty();
                        if (!RealmsBackupScreen.this.noBackups && RealmsBackupScreen.this.downloadButton != null) {
                            RealmsBackupScreen.this.downloadButton.active = true;
                        }

                        if (RealmsBackupScreen.this.f_314156_ != null) {
                            RealmsBackupScreen.this.f_314156_.children().clear();

                            for (Backup backup : RealmsBackupScreen.this.backups) {
                                RealmsBackupScreen.this.f_314156_.addEntry(backup);
                            }
                        }
                    });
                } catch (RealmsServiceException realmsserviceexception) {
                    RealmsBackupScreen.LOGGER.error("Couldn't request backups", (Throwable)realmsserviceexception);
                }
            }
        }).start();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    private void downloadClicked() {
        Component component = Component.translatable("mco.configure.world.restore.download.question.line1");
        Component component1 = Component.translatable("mco.configure.world.restore.download.question.line2");
        this.minecraft
            .setScreen(
                new RealmsLongConfirmationScreen(
                    p_325103_ -> {
                        if (p_325103_) {
                            this.minecraft
                                .setScreen(
                                    new RealmsLongRunningMcoTaskScreen(
                                        this.lastScreen.getNewScreen(),
                                        new DownloadTask(
                                            this.serverData.id,
                                            this.slotId,
                                            this.serverData.name
                                                + " ("
                                                + this.serverData.slots.get(this.serverData.activeSlot).getSlotName(this.serverData.activeSlot)
                                                + ")",
                                            this
                                        )
                                    )
                                );
                        } else {
                            this.minecraft.setScreen(this);
                        }
                    },
                    RealmsLongConfirmationScreen.Type.INFO,
                    component,
                    component1,
                    true
                )
            );
    }

    @OnlyIn(Dist.CLIENT)
    class BackupObjectSelectionList extends ContainerObjectSelectionList<RealmsBackupScreen.Entry> {
        private static final int f_315918_ = 36;

        public BackupObjectSelectionList() {
            super(
                Minecraft.getInstance(),
                RealmsBackupScreen.this.width,
                RealmsBackupScreen.this.f_314075_.m_319781_(),
                RealmsBackupScreen.this.f_314075_.getHeaderHeight(),
                36
            );
        }

        public void addEntry(Backup pBackup) {
            this.addEntry(RealmsBackupScreen.this.new Entry(pBackup));
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 36 + this.headerHeight;
        }

        @Override
        public int getRowWidth() {
            return 300;
        }
    }

    @OnlyIn(Dist.CLIENT)
    class Entry extends ContainerObjectSelectionList.Entry<RealmsBackupScreen.Entry> {
        private static final int Y_PADDING = 2;
        private final Backup backup;
        @Nullable
        private Button changesButton;
        @Nullable
        private Button restoreButton;
        private final List<AbstractWidget> children = new ArrayList<>();

        public Entry(final Backup pBackup) {
            this.backup = pBackup;
            this.populateChangeList(pBackup);
            if (!pBackup.changeList.isEmpty()) {
                this.restoreButton = Button.builder(
                        RealmsBackupScreen.HAS_CHANGES_TOOLTIP,
                        p_325112_ -> RealmsBackupScreen.this.minecraft.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, this.backup))
                    )
                    .width(8 + RealmsBackupScreen.this.font.width(RealmsBackupScreen.HAS_CHANGES_TOOLTIP))
                    .createNarration(p_325109_ -> CommonComponents.joinForNarration(Component.translatable("mco.backup.narration", this.m_323206_()), p_325109_.get()))
                    .build();
                this.children.add(this.restoreButton);
            }

            if (!RealmsBackupScreen.this.serverData.expired) {
                this.changesButton = Button.builder(RealmsBackupScreen.RESTORE_TOOLTIP, p_325108_ -> this.m_323294_())
                    .width(8 + RealmsBackupScreen.this.font.width(RealmsBackupScreen.HAS_CHANGES_TOOLTIP))
                    .createNarration(p_325111_ -> CommonComponents.joinForNarration(Component.translatable("mco.backup.narration", this.m_323206_()), p_325111_.get()))
                    .build();
                this.children.add(this.changesButton);
            }
        }

        private void populateChangeList(Backup pBackup) {
            int i = RealmsBackupScreen.this.backups.indexOf(pBackup);
            if (i != RealmsBackupScreen.this.backups.size() - 1) {
                Backup backup = RealmsBackupScreen.this.backups.get(i + 1);

                for (String s : pBackup.metadata.keySet()) {
                    if (!s.contains("uploaded") && backup.metadata.containsKey(s)) {
                        if (!pBackup.metadata.get(s).equals(backup.metadata.get(s))) {
                            this.addToChangeList(s);
                        }
                    } else {
                        this.addToChangeList(s);
                    }
                }
            }
        }

        private void addToChangeList(String pChange) {
            if (pChange.contains("uploaded")) {
                String s = DateFormat.getDateTimeInstance(3, 3).format(this.backup.lastModifiedDate);
                this.backup.changeList.put(pChange, s);
                this.backup.setUploadedVersion(true);
            } else {
                this.backup.changeList.put(pChange, this.backup.metadata.get(pChange));
            }
        }

        private String m_323206_() {
            return DateFormat.getDateTimeInstance(3, 3).format(this.backup.lastModifiedDate);
        }

        private void m_323294_() {
            Component component = RealmsUtil.convertToAgePresentationFromInstant(this.backup.lastModifiedDate);
            Component component1 = Component.translatable("mco.configure.world.restore.question.line1", this.m_323206_(), component);
            Component component2 = Component.translatable("mco.configure.world.restore.question.line2");
            RealmsBackupScreen.this.minecraft
                .setScreen(
                    new RealmsLongConfirmationScreen(
                        p_325110_ -> {
                            if (p_325110_) {
                                RealmsBackupScreen.this.minecraft
                                    .setScreen(
                                        new RealmsLongRunningMcoTaskScreen(
                                            RealmsBackupScreen.this.lastScreen.getNewScreen(),
                                            new RestoreTask(this.backup, RealmsBackupScreen.this.serverData.id, RealmsBackupScreen.this.lastScreen)
                                        )
                                    );
                            } else {
                                RealmsBackupScreen.this.minecraft.setScreen(RealmsBackupScreen.this);
                            }
                        },
                        RealmsLongConfirmationScreen.Type.WARNING,
                        component1,
                        component2,
                        true
                    )
                );
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.children;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.children;
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
            int i = pTop + pHeight / 2;
            int j = i - 9 - 2;
            int k = i + 2;
            int l = this.backup.isUploadedVersion() ? -8388737 : -1;
            pGuiGraphics.drawString(
                RealmsBackupScreen.this.font, Component.translatable("mco.backup.entry", RealmsUtil.convertToAgePresentationFromInstant(this.backup.lastModifiedDate)), pLeft, j, l, false
            );
            pGuiGraphics.drawString(RealmsBackupScreen.this.font, this.getMediumDatePresentation(this.backup.lastModifiedDate), pLeft, k, 5000268, false);
            int i1 = 0;
            int j1 = pTop + pHeight / 2 - 10;
            if (this.changesButton != null) {
                i1 += this.changesButton.getWidth() + 8;
                this.changesButton.setX(pLeft + pWidth - i1);
                this.changesButton.setY(j1);
                this.changesButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            }

            if (this.restoreButton != null) {
                i1 += this.restoreButton.getWidth() + 8;
                this.restoreButton.setX(pLeft + pWidth - i1);
                this.restoreButton.setY(j1);
                this.restoreButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            }
        }

        private String getMediumDatePresentation(Date pDate) {
            return DateFormat.getDateTimeInstance(3, 3).format(pDate);
        }
    }
}
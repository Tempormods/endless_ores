package net.minecraft.client.gui.screens.reporting;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.chat.report.ReportReason;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ReportReasonSelectionScreen extends Screen {
    private static final Component REASON_TITLE = Component.translatable("gui.abuseReport.reason.title");
    private static final Component REASON_DESCRIPTION = Component.translatable("gui.abuseReport.reason.description");
    private static final Component READ_INFO_LABEL = Component.translatable("gui.abuseReport.read_info");
    private static final int f_315225_ = 320;
    private static final int f_315831_ = 62;
    private static final int PADDING = 4;
    @Nullable
    private final Screen lastScreen;
    @Nullable
    private ReportReasonSelectionScreen.ReasonSelectionList reasonSelectionList;
    @Nullable
    ReportReason currentlySelectedReason;
    private final Consumer<ReportReason> onSelectedReason;
    final HeaderAndFooterLayout f_314586_ = new HeaderAndFooterLayout(this);

    public ReportReasonSelectionScreen(@Nullable Screen pLastScreen, @Nullable ReportReason pCurrentlySelectedReason, Consumer<ReportReason> pOnSelectedReason) {
        super(REASON_TITLE);
        this.lastScreen = pLastScreen;
        this.currentlySelectedReason = pCurrentlySelectedReason;
        this.onSelectedReason = pOnSelectedReason;
    }

    @Override
    protected void init() {
        this.f_314586_.m_324480_(REASON_TITLE, this.font);
        LinearLayout linearlayout = this.f_314586_.addToContents(LinearLayout.vertical().spacing(4));
        this.reasonSelectionList = linearlayout.addChild(new ReportReasonSelectionScreen.ReasonSelectionList(this.minecraft));
        ReportReasonSelectionScreen.ReasonSelectionList.Entry reportreasonselectionscreen$reasonselectionlist$entry = Optionull.map(
            this.currentlySelectedReason, this.reasonSelectionList::findEntry
        );
        this.reasonSelectionList.setSelected(reportreasonselectionscreen$reasonselectionlist$entry);
        linearlayout.addChild(SpacerElement.height(this.m_321777_()));
        LinearLayout linearlayout1 = this.f_314586_.addToFooter(LinearLayout.horizontal().spacing(8));
        linearlayout1.addChild(Button.builder(READ_INFO_LABEL, ConfirmLinkScreen.confirmLink(this, "https://aka.ms/aboutjavareporting")).build());
        linearlayout1.addChild(Button.builder(CommonComponents.GUI_DONE, p_325403_ -> {
            ReportReasonSelectionScreen.ReasonSelectionList.Entry reportreasonselectionscreen$reasonselectionlist$entry1 = this.reasonSelectionList.getSelected();
            if (reportreasonselectionscreen$reasonselectionlist$entry1 != null) {
                this.onSelectedReason.accept(reportreasonselectionscreen$reasonselectionlist$entry1.getReason());
            }

            this.minecraft.setScreen(this.lastScreen);
        }).build());
        this.f_314586_.visitWidgets(p_325405_ -> {
            AbstractWidget abstractwidget = this.addRenderableWidget(p_325405_);
        });
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.f_314586_.arrangeElements();
        if (this.reasonSelectionList != null) {
            this.reasonSelectionList.m_321569_(this.width, this.m_318650_(), this.f_314586_.getHeaderHeight());
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.fill(this.contentLeft(), this.descriptionTop(), this.contentRight(), this.descriptionBottom(), -16777216);
        pGuiGraphics.renderOutline(this.contentLeft(), this.descriptionTop(), this.m_321549_(), this.m_321777_(), -1);
        pGuiGraphics.drawString(this.font, REASON_DESCRIPTION, this.contentLeft() + 4, this.descriptionTop() + 4, -1);
        ReportReasonSelectionScreen.ReasonSelectionList.Entry reportreasonselectionscreen$reasonselectionlist$entry = this.reasonSelectionList.getSelected();
        if (reportreasonselectionscreen$reasonselectionlist$entry != null) {
            int i = this.contentLeft() + 4 + 16;
            int j = this.contentRight() - 4;
            int k = this.descriptionTop() + 4 + 9 + 2;
            int l = this.descriptionBottom() - 4;
            int i1 = j - i;
            int j1 = l - k;
            int k1 = this.font.wordWrapHeight(reportreasonselectionscreen$reasonselectionlist$entry.reason.description(), i1);
            pGuiGraphics.drawWordWrap(this.font, reportreasonselectionscreen$reasonselectionlist$entry.reason.description(), i, k + (j1 - k1) / 2, i1, -1);
        }
    }

    private int contentLeft() {
        return (this.width - 320) / 2;
    }

    private int contentRight() {
        return (this.width + 320) / 2;
    }

    private int descriptionTop() {
        return this.descriptionBottom() - this.m_321777_();
    }

    private int descriptionBottom() {
        return this.height - this.f_314586_.getFooterHeight() - 4;
    }

    private int m_321549_() {
        return 320;
    }

    private int m_321777_() {
        return 62;
    }

    int m_318650_() {
        return this.f_314586_.m_319781_() - this.m_321777_() - 8;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @OnlyIn(Dist.CLIENT)
    public class ReasonSelectionList extends ObjectSelectionList<ReportReasonSelectionScreen.ReasonSelectionList.Entry> {
        public ReasonSelectionList(final Minecraft pMinecraft) {
            super(
                pMinecraft,
                ReportReasonSelectionScreen.this.width,
                ReportReasonSelectionScreen.this.m_318650_(),
                ReportReasonSelectionScreen.this.f_314586_.getHeaderHeight(),
                18
            );

            for (ReportReason reportreason : ReportReason.values()) {
                this.addEntry(new ReportReasonSelectionScreen.ReasonSelectionList.Entry(reportreason));
            }
        }

        @Nullable
        public ReportReasonSelectionScreen.ReasonSelectionList.Entry findEntry(ReportReason pReason) {
            return this.children().stream().filter(p_239293_ -> p_239293_.reason == pReason).findFirst().orElse(null);
        }

        @Override
        public int getRowWidth() {
            return 320;
        }

        public void setSelected(@Nullable ReportReasonSelectionScreen.ReasonSelectionList.Entry pSelected) {
            super.setSelected(pSelected);
            ReportReasonSelectionScreen.this.currentlySelectedReason = pSelected != null ? pSelected.getReason() : null;
        }

        @OnlyIn(Dist.CLIENT)
        public class Entry extends ObjectSelectionList.Entry<ReportReasonSelectionScreen.ReasonSelectionList.Entry> {
            final ReportReason reason;

            public Entry(final ReportReason pReason) {
                this.reason = pReason;
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
                int i = pLeft + 1;
                int j = pTop + (pHeight - 9) / 2 + 1;
                pGuiGraphics.drawString(ReportReasonSelectionScreen.this.font, this.reason.title(), i, j, -1);
            }

            @Override
            public Component getNarration() {
                return Component.translatable("gui.abuseReport.reason.narration", this.reason.title(), this.reason.description());
            }

            /**
             * Called when a mouse button is clicked within the GUI element.
             * <p>
             * @return {@code true} if the event is consumed, {@code false} otherwise.
             * @param pMouseX the X coordinate of the mouse.
             * @param pMouseY the Y coordinate of the mouse.
             * @param pButton the button that was clicked.
             */
            @Override
            public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
                ReasonSelectionList.this.setSelected(this);
                return super.mouseClicked(pMouseX, pMouseY, pButton);
            }

            public ReportReason getReason() {
                return this.reason;
            }
        }
    }
}
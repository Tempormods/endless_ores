package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsPlayerScreen extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Component f_315677_ = Component.translatable("mco.configure.world.players.title");
    static final Component QUESTION_TITLE = Component.translatable("mco.question");
    private static final int f_315644_ = 8;
    final HeaderAndFooterLayout f_314399_ = new HeaderAndFooterLayout(this);
    private final RealmsConfigureWorldScreen lastScreen;
    final RealmsServer serverData;
    @Nullable
    private RealmsPlayerScreen.InvitedObjectSelectionList f_314411_;
    boolean stateChanged;

    public RealmsPlayerScreen(RealmsConfigureWorldScreen pLastScreen, RealmsServer pServerData) {
        super(f_315677_);
        this.lastScreen = pLastScreen;
        this.serverData = pServerData;
    }

    @Override
    public void init() {
        this.f_314399_.m_324480_(f_315677_, this.font);
        this.f_314411_ = this.f_314399_.addToContents(new RealmsPlayerScreen.InvitedObjectSelectionList());
        this.m_323661_();
        LinearLayout linearlayout = this.f_314399_.addToFooter(LinearLayout.horizontal().spacing(8));
        linearlayout.addChild(
            Button.builder(
                    Component.translatable("mco.configure.world.buttons.invite"),
                    p_280732_ -> this.minecraft.setScreen(new RealmsInviteScreen(this.lastScreen, this, this.serverData))
                )
                .build()
        );
        linearlayout.addChild(Button.builder(CommonComponents.GUI_BACK, p_325135_ -> this.onClose()).build());
        this.f_314399_.visitWidgets(p_325137_ -> {
            AbstractWidget abstractwidget = this.addRenderableWidget(p_325137_);
        });
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.f_314399_.arrangeElements();
        if (this.f_314411_ != null) {
            this.f_314411_.m_319425_(this.width, this.f_314399_);
        }
    }

    void m_323661_() {
        if (this.f_314411_ != null) {
            this.f_314411_.children().clear();

            for (PlayerInfo playerinfo : this.serverData.players) {
                this.f_314411_.children().add(new RealmsPlayerScreen.Entry(playerinfo));
            }
        }
    }

    @Override
    public void onClose() {
        this.backButtonClicked();
    }

    private void backButtonClicked() {
        if (this.stateChanged) {
            this.minecraft.setScreen(this.lastScreen.getNewScreen());
        } else {
            this.minecraft.setScreen(this.lastScreen);
        }
    }

    @OnlyIn(Dist.CLIENT)
    class Entry extends ContainerObjectSelectionList.Entry<RealmsPlayerScreen.Entry> {
        private static final Component f_315568_ = Component.translatable("mco.configure.world.invites.normal.tooltip");
        private static final Component f_314898_ = Component.translatable("mco.configure.world.invites.ops.tooltip");
        private static final Component f_314763_ = Component.translatable("mco.configure.world.invites.remove.tooltip");
        private static final ResourceLocation f_314103_ = new ResourceLocation("player_list/make_operator");
        private static final ResourceLocation f_315396_ = new ResourceLocation("player_list/remove_operator");
        private static final ResourceLocation f_315491_ = new ResourceLocation("player_list/remove_player");
        private static final int f_314289_ = 8;
        private static final int f_315129_ = 7;
        private final PlayerInfo playerInfo;
        private final Button removeButton;
        private final Button makeOpButton;
        private final Button removeOpButton;

        public Entry(final PlayerInfo pPlayerInfo) {
            this.playerInfo = pPlayerInfo;
            int i = RealmsPlayerScreen.this.serverData.players.indexOf(this.playerInfo);
            this.makeOpButton = SpriteIconButton.builder(f_315568_, p_325150_ -> this.m_321065_(i), false)
                .sprite(f_314103_, 8, 7)
                .width(16 + RealmsPlayerScreen.this.font.width(f_315568_))
                .m_323620_(
                    p_325144_ -> CommonComponents.joinForNarration(
                            Component.translatable("mco.invited.player.narration", pPlayerInfo.getName()),
                            p_325144_.get(),
                            Component.translatable("narration.cycle_button.usage.focused", f_314898_)
                        )
                )
                .build();
            this.removeOpButton = SpriteIconButton.builder(f_314898_, p_325146_ -> this.m_323386_(i), false)
                .sprite(f_315396_, 8, 7)
                .width(16 + RealmsPlayerScreen.this.font.width(f_314898_))
                .m_323620_(
                    p_325142_ -> CommonComponents.joinForNarration(
                            Component.translatable("mco.invited.player.narration", pPlayerInfo.getName()),
                            p_325142_.get(),
                            Component.translatable("narration.cycle_button.usage.focused", f_315568_)
                        )
                )
                .build();
            this.removeButton = SpriteIconButton.builder(f_314763_, p_325152_ -> this.m_324131_(i), false)
                .sprite(f_315491_, 8, 7)
                .width(16 + RealmsPlayerScreen.this.font.width(f_314763_))
                .m_323620_(p_325148_ -> CommonComponents.joinForNarration(Component.translatable("mco.invited.player.narration", pPlayerInfo.getName()), p_325148_.get()))
                .build();
            this.m_324967_();
        }

        private void m_321065_(int p_333700_) {
            RealmsClient realmsclient = RealmsClient.create();
            UUID uuid = RealmsPlayerScreen.this.serverData.players.get(p_333700_).getUuid();

            try {
                this.m_322209_(realmsclient.op(RealmsPlayerScreen.this.serverData.id, uuid));
            } catch (RealmsServiceException realmsserviceexception) {
                RealmsPlayerScreen.LOGGER.error("Couldn't op the user", (Throwable)realmsserviceexception);
            }

            this.m_324967_();
        }

        private void m_323386_(int p_328404_) {
            RealmsClient realmsclient = RealmsClient.create();
            UUID uuid = RealmsPlayerScreen.this.serverData.players.get(p_328404_).getUuid();

            try {
                this.m_322209_(realmsclient.deop(RealmsPlayerScreen.this.serverData.id, uuid));
            } catch (RealmsServiceException realmsserviceexception) {
                RealmsPlayerScreen.LOGGER.error("Couldn't deop the user", (Throwable)realmsserviceexception);
            }

            this.m_324967_();
        }

        private void m_324131_(int p_328197_) {
            if (p_328197_ >= 0 && p_328197_ < RealmsPlayerScreen.this.serverData.players.size()) {
                PlayerInfo playerinfo = RealmsPlayerScreen.this.serverData.players.get(p_328197_);
                RealmsConfirmScreen realmsconfirmscreen = new RealmsConfirmScreen(p_325140_ -> {
                    if (p_325140_) {
                        RealmsClient realmsclient = RealmsClient.create();

                        try {
                            realmsclient.uninvite(RealmsPlayerScreen.this.serverData.id, playerinfo.getUuid());
                        } catch (RealmsServiceException realmsserviceexception) {
                            RealmsPlayerScreen.LOGGER.error("Couldn't uninvite user", (Throwable)realmsserviceexception);
                        }

                        RealmsPlayerScreen.this.serverData.players.remove(p_328197_);
                        RealmsPlayerScreen.this.m_323661_();
                    }

                    RealmsPlayerScreen.this.stateChanged = true;
                    RealmsPlayerScreen.this.minecraft.setScreen(RealmsPlayerScreen.this);
                }, RealmsPlayerScreen.QUESTION_TITLE, Component.translatable("mco.configure.world.uninvite.player", playerinfo.getName()));
                RealmsPlayerScreen.this.minecraft.setScreen(realmsconfirmscreen);
            }
        }

        private void m_322209_(Ops p_335160_) {
            for (PlayerInfo playerinfo : RealmsPlayerScreen.this.serverData.players) {
                playerinfo.setOperator(p_335160_.ops.contains(playerinfo.getName()));
            }
        }

        private void m_324967_() {
            this.makeOpButton.visible = !this.playerInfo.isOperator();
            this.removeOpButton.visible = !this.makeOpButton.visible;
        }

        private Button m_318770_() {
            return this.makeOpButton.visible ? this.makeOpButton : this.removeOpButton;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.m_318770_(), this.removeButton);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.m_318770_(), this.removeButton);
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
            int i;
            if (!this.playerInfo.getAccepted()) {
                i = -6250336;
            } else if (this.playerInfo.getOnline()) {
                i = 8388479;
            } else {
                i = -1;
            }

            int j = pTop + pHeight / 2 - 16;
            RealmsUtil.renderPlayerFace(pGuiGraphics, pLeft, j, 32, this.playerInfo.getUuid());
            int k = pTop + pHeight / 2 - 9 / 2;
            pGuiGraphics.drawString(RealmsPlayerScreen.this.font, this.playerInfo.getName(), pLeft + 8 + 32, k, i, false);
            int l = pTop + pHeight / 2 - 10;
            int i1 = pLeft + pWidth - this.removeButton.getWidth();
            this.removeButton.setPosition(i1, l);
            this.removeButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            int j1 = i1 - this.m_318770_().getWidth() - 8;
            this.makeOpButton.setPosition(j1, l);
            this.makeOpButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            this.removeOpButton.setPosition(j1, l);
            this.removeOpButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }
    }

    @OnlyIn(Dist.CLIENT)
    class InvitedObjectSelectionList extends ContainerObjectSelectionList<RealmsPlayerScreen.Entry> {
        private static final int f_314843_ = 36;

        public InvitedObjectSelectionList() {
            super(
                Minecraft.getInstance(),
                RealmsPlayerScreen.this.width,
                RealmsPlayerScreen.this.f_314399_.m_319781_(),
                RealmsPlayerScreen.this.f_314399_.getHeaderHeight(),
                36
            );
            this.setRenderHeader(true, (int)(9.0F * 1.5F));
        }

        @Override
        protected void renderHeader(GuiGraphics p_329500_, int p_331955_, int p_330781_) {
            String s = RealmsPlayerScreen.this.serverData.players != null ? Integer.toString(RealmsPlayerScreen.this.serverData.players.size()) : "0";
            Component component = Component.translatable("mco.configure.world.invited.number", s).withStyle(ChatFormatting.UNDERLINE);
            p_329500_.drawString(
                RealmsPlayerScreen.this.font,
                component,
                p_331955_ + this.getRowWidth() / 2 - RealmsPlayerScreen.this.font.width(component) / 2,
                p_330781_,
                -1,
                false
            );
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * this.itemHeight + this.headerHeight;
        }

        @Override
        public int getRowWidth() {
            return 300;
        }
    }
}